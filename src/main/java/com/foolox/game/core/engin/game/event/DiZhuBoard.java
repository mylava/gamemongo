package com.foolox.game.core.engin.game.event;

import com.foolox.game.common.model.Summary;
import com.foolox.game.common.model.SummaryPlayer;
import com.foolox.game.common.repo.domain.ClientSession;
import com.foolox.game.common.repo.domain.GamePlayway;
import com.foolox.game.common.repo.domain.GameRoom;
import com.foolox.game.common.util.FooloxGameTaskUtil;
import com.foolox.game.common.util.FooloxUtils;
import com.foolox.game.common.util.GameUtils;
import com.foolox.game.constants.Command;
import com.foolox.game.constants.PVAConsumeActionEnum;
import com.foolox.game.constants.PVAInComeActionEnum;
import com.foolox.game.constants.PlayerType;
import com.foolox.game.core.engin.game.ActionTaskUtils;
import com.foolox.game.core.engin.game.CardType;
import com.foolox.game.core.engin.game.pva.PVAOperatorResult;
import com.foolox.game.core.engin.game.pva.PvaTools;
import com.foolox.game.core.engin.game.state.PlayerEvent;
import com.foolox.game.core.logic.CardsTypeEnum;
import org.apache.commons.lang3.ArrayUtils;

import java.util.Arrays;
import java.util.List;

/**
 * comment: 牌局，用于描述当前牌局的内容 ，
 * 1、随机排序生成的 当前 待起牌（麻将、德州有/斗地主无）
 * 2、玩家 手牌
 * 3、玩家信息
 * 4、当前牌
 * 5、当前玩家
 * 6、房间/牌桌信息
 * 7、其他附加信息
 * 数据结构内存占用 78 byte ， 一副牌序列化到 数据库 占用的存储空间约为 78 byt， 数据库字段长度约为 20
 *
 * @author: lipengfei
 * @date: 30/05/2019
 */
public class DiZhuBoard extends Board {

    /**
     * 翻底牌(斗地主使用）
     */
    @Override
    public byte[] pollLastHands() {
        return ArrayUtils.subarray(this.getCards(), this.getCards().length - 3, this.getCards().length);
    }

    /**
     * 倍数，例如：底牌有大王翻两倍，底牌有小王 翻一倍，底牌是顺子 翻两倍 ====
     */
    @Override
    public int calcRatio() {
        return 1;
    }

    @Override
    public TakeCards takeCards(GamePlayer gamePlayer, String playerType, TakeCards current) {
        return new TakeDiZhuCards(gamePlayer);
    }


    /**
     * 找到玩家
     * @param userid
     * @return
     */
    @Override
    public GamePlayer getGamePlayer(String userid) {
        GamePlayer target = null;
        for (GamePlayer temp : this.getGamePlayers()) {
            if (temp.getPlayuserId().equals(userid)) {
                target = temp;
                break;
            }
        }
        return target;
    }

    /**
     * 找到玩家的 位置
     * @param userid
     * @return
     */
    @Override
    public int index(String userid) {
        int index = 0;
        for (int i = 0; i < this.getGamePlayers().length; i++) {
            GamePlayer temp = this.getGamePlayers()[i];
            if (temp.getPlayuserId().equals(userid)) {
                index = i;
                break;
            }
        }
        return index;
    }


    /**
     * 找到下一个玩家
     * @param index
     * @return
     */
    @Override
    public GamePlayer next(int index) {
        GamePlayer catchPlayer = null;
        if (index == (this.getGamePlayers().length - 1)) {    //fixed
            index = -1;
        }
        for (int i = index + 1; i < this.getGamePlayers().length; ) {
            GamePlayer player = this.getGamePlayers()[i];
            if (player.isDocatch() == false) {
                catchPlayer = player;
                break;
            } else if (player.isRandomcard()) {    //重新遍历一遍，发现找到了地主牌的人，终止查找
                break;
            } else if (i == (this.getGamePlayers().length - 1)) {
                i = 0;
                continue;
            }
            i++;
        }
        return catchPlayer;
    }


    @Override
    public GamePlayer nextPlayer(int index) {
        if (index == (this.getGamePlayers().length - 1)) {
            index = 0;
        } else {
            index = index + 1;
        }
        return this.getGamePlayers()[index];
    }

    /**
     *
     * @param gamePlayer
     * @param allow
     * @param playCards
     * @return
     */
    @Override
    public TakeCards takecard(GamePlayer gamePlayer, boolean allow, byte[] playCards) {
        return new TakeDiZhuCards(gamePlayer, allow, playCards);
    }

    /**
     * 当前玩家随机出牌，能管住当前出牌的 最小牌
     * @param gamePlayer
     * @return
     */
    @Override
    public TakeCards takecard(GamePlayer gamePlayer) {
        return new TakeDiZhuCards(gamePlayer);
    }

    /**
     * 当前玩家随机出牌，能管住当前出牌的 最小牌
     * @param gamePlayer
     * @return
     */
    @Override
    public TakeCards takecard(GamePlayer gamePlayer, TakeCards last) {
        return new TakeDiZhuCards(gamePlayer, last);
    }

    /**
     * 当前玩家随机出牌，能管住当前出牌的 最小牌
     * @param gamePlayer
     * @return
     */
    public TakeCards cardtip(GamePlayer gamePlayer, TakeCards last) {
        return new TakeDiZhuCards(gamePlayer, last, false);
    }

    /**
     * 顺序提示玩家出牌
     *
     * @param player
     * @param tipcards
     * @return
     */
    public TakeCards getCardTips(GamePlayer player, byte[] tipcards) {
        return new TakeDiZhuCards(player, tipcards);
    }

    @Override
    public boolean isWin() {
        boolean win = false;
        if (this.getLast() != null && this.getLast().getCardsnum() == 0) {//出完了
            win = true;
        }
        return win;
    }

    @Override
    public TakeCards takeCardsRequest(GameRoom gameRoom, Board board, GamePlayer gamePlayer,
                                      boolean auto, byte[] playCards) {
        TakeCards takeCards = null;
        boolean automic = false;
        //超时了 ， 执行自动出牌
        if ((auto || playCards != null)) {
            CardType playCardType = null;
            if (playCards != null && playCards.length > 0) {
                playCardType = ActionTaskUtils.identification(playCards);
            }
            if (playCardType == null || playCardType.getCardtype() > 0) {
                if (board.getLast() == null || board.getLast().getUserid().equals(gamePlayer.getPlayuserId())) {    //当前无出牌信息，刚开始出牌，或者出牌无玩家 压
                    /**
                     * 超时处理，如果当前是托管的或玩家超时，直接从最小的牌开始出，如果是 AI，则 需要根据AI级别（低级/中级/高级） 计算出牌 ， 目前先不管，直接从最小的牌开始出
                     */
                    takeCards = board.takecard(gamePlayer, true, playCards);
                } else {
                    if (playCards == null) {
                        takeCards = board.takecard(gamePlayer, board.getLast());
                    } else {
                        CardType lastCardType = ActionTaskUtils.identification(board.getLast().getCards());
                        if (playCardType.getCardtype() > 0 && ActionTaskUtils.allow(playCardType, lastCardType)) {//合规，允许出牌
                            takeCards = board.takecard(gamePlayer, true, playCards);
                        }//不合规的牌 ， 需要通知客户端 出牌不符合规则 ， 此处放在服务端判断，防外挂
                    }
                }
            }
        } else {
            takeCards = new TakeDiZhuCards();
            takeCards.setUserid(gamePlayer.getPlayuserId());
        }
        if (takeCards != null) {        //通知出牌
            takeCards.setCardsnum(gamePlayer.getCards().length);
            takeCards.setAllow(true);
            if (takeCards.getCards() != null) {
                Arrays.sort(takeCards.getCards());
            }

            if (takeCards.getCards() != null) {
                board.setLast(takeCards);
                takeCards.setDonot(false);    //出牌
            } else {
                takeCards.setDonot(true);    //不出牌
            }
            if (takeCards.getCardType() != null && (takeCards.getCardType().getCardtype() == CardsTypeEnum.TEN.getType() || takeCards.getCardType().getCardtype() == CardsTypeEnum.ELEVEN.getType())) {
                takeCards.setBomb(true);
                ActionTaskUtils.doBomb(board, true);
                ActionTaskUtils.sendEvent(Command.RATIO, new BoardRatio(takeCards.isBomb(), false, board.getRatio()), gameRoom);
            }

            GamePlayer next = board.nextPlayer(board.index(gamePlayer.getPlayuserId()));
            if (next != null) {
                takeCards.setNextplayer(next.getPlayuserId());
                board.setNextplayer(new NextPlayer(next.getPlayuserId(), false));

                if (board.getLast() != null && board.getLast().getUserid().equals(next.getPlayuserId())) {    //当前无出牌信息，刚开始出牌，或者出牌无玩家 压
                    automic = true;
                }
                takeCards.setAutomic(automic);
            }
            if (board.isWin()) {//出完了
                board.setWinner(gamePlayer.getPlayuserId());
                takeCards.setOver(true);
            }
            /**
             * 放到 Board的列表里去，如果是不洗牌玩法，则直接将出牌结果 重新发牌
             */
            if (takeCards.getCards() != null && takeCards.getCards().length > 0) {
                for (byte temp : takeCards.getCards()) {
                    board.getHistory().add(temp);
                }
            }
            //保存board信息到缓存
            FooloxUtils.setBoardByRoomId(gameRoom.getId(), board);
            /**
             * 判断下当前玩家是不是和最后一手牌 是一伙的，如果是一伙的，手机端提示 就是 不要， 如果不是一伙的，就提示要不起
             */
            if (gamePlayer.getPlayuserId().equals(board.getBanker())) { //当前玩家是地主
                takeCards.setSameside(false);
            } else {
                if (board.getLast().getUserid().equals(board.getBanker())) { //最后一把是地主出的，然而我不是地主
                    takeCards.setSameside(false);
                } else {
                    takeCards.setSameside(true);
                }
            }
            /**
             * 移除定时器，然后重新设置
             */
            FooloxGameTaskUtil.getExpireCache().remove(gameRoom.getRoomid());


            if (takeCards.getCards() != null && takeCards.getCards().length == 1) {
                takeCards.setCard(takeCards.getCards()[0]);
            }

            ActionTaskUtils.sendEvent(Command.TAKECARDS, takeCards, gameRoom);

            /**
             * 牌出完了就算赢了
             */
            if (board.isWin()) {//出完了
                GameUtils.getGame(gameRoom.getPlaywayId()).change(gameRoom, PlayerEvent.ALLCARDS, 0);    //赢了，通知结算
                takeCards.setNextplayer(null);
            } else {
                ClientSession nextClientSession = ActionTaskUtils.getClientSession(gameRoom.getId(), takeCards.getNextplayer());
                if (nextClientSession != null) {
                    if (PlayerType.NORMAL==nextClientSession.getPlayerType()) {
                        GameUtils.getGame(gameRoom.getPlaywayId()).change(gameRoom, PlayerEvent.PLAYCARDS, 25);    //应该从 游戏后台配置参数中获取
                    } else {
                        GameUtils.getGame(gameRoom.getPlaywayId()).change(gameRoom, PlayerEvent.PLAYCARDS, 3);    //应该从游戏后台配置参数中获取
                    }
                }
            }
        } else {
            takeCards = new TakeDiZhuCards();
            takeCards.setAllow(false);
            ActionTaskUtils.sendEvent(Command.TAKECARDS, takeCards, gameRoom);
        }
        return takeCards;
    }

    @Override
    public void dealRequest(GameRoom gameRoom, Board board, boolean reverse, String nextplayer) {
        /**
         * 斗地主无发牌动作
         */
    }

    @Override
    public void playcards(Board board, GameRoom gameRoom, GamePlayer gamePlayer) {
    }

    @Override
    public Summary summary(Board board, GameRoom gameRoom, GamePlayway playway) {
        Summary summary = new Summary(gameRoom.getId(), board.getId(), board.getRatio(), board.getRatio() * playway.getScore());
        long dizhuScore = 0;
        boolean dizhuWin = board.getWinner().equals(board.getBanker());

        List<ClientSession> clientSessionList = FooloxUtils.getRoomClientSessionList(gameRoom.getId());

        ClientSession dizhuPlayerUser = getPlayerClient(clientSessionList, board.getBanker());
        int temp = summary.getScore() * (board.getGamePlayers().length - 1);
        SummaryPlayer dizhuSummaryPlayer = null;
        PVAOperatorResult result = null;
        boolean gameRoomOver = false;    //解散房价

        for (GamePlayer gamePlayer : board.getGamePlayers()) {
            ClientSession clientSession = getPlayerClient(clientSessionList, gamePlayer.getPlayuserId());
            SummaryPlayer summaryPlayer = new SummaryPlayer(gamePlayer.getPlayuserId(), clientSession.getUsername(), board.getRatio(), board.getRatio() * playway.getScore(), false, gamePlayer.getPlayuserId().equals(board.getBanker()));
            /**
             * 找到对应的玩家结算信息
             */
            if (gamePlayer.getPlayuserId().equals(board.getBanker())) {
                dizhuSummaryPlayer = summaryPlayer;
            }
            if (dizhuWin) {
                if (gamePlayer.getPlayuserId().equals(board.getBanker())) {
                    summaryPlayer.setWin(true);
                } else {
                    /**
                     * 扣 农民的 金币 , 扣除金币的时候需要最好做一下金币的校验，例如：签名验证是否由系统修改的 金币余额，并记录金币扣除的日志，用于账号账单信息
                     */
                    if (clientSession.getCoins() <= summaryPlayer.getScore()) {
                        summaryPlayer.setScore(clientSession.getCoins());//还有多少，扣你多少
                        summaryPlayer.setGameover(true);                //金币不够了，破产，重新充值或领取奖励恢复状态
                        gameRoomOver = true;
                    }
                    dizhuScore = dizhuScore + summaryPlayer.getScore();
                    result = PvaTools.getGoldCoins().consume(clientSession, PVAConsumeActionEnum.LOST.toString(), summaryPlayer.getScore());
                    summaryPlayer.setBalance(result.getBalance());
                }
            } else {    //地主输了
                if (!gamePlayer.getPlayuserId().equals(board.getBanker())) {
                    summaryPlayer.setWin(true);
                    if (dizhuPlayerUser.getCoins() < temp) {    //金币不够扣
                        summaryPlayer.setScore(dizhuPlayerUser.getCoins() / (board.getGamePlayers().length - 1));
                        gameRoomOver = true;
                    }
                    dizhuScore = dizhuScore + summaryPlayer.getScore();

                    /**
                     * 应该共用一个 扣除个人虚拟资产的 全局对象，用于处理个人虚拟资产
                     */
                    result = PvaTools.getGoldCoins().income(clientSession, PVAInComeActionEnum.WIN.toString(), summaryPlayer.getScore());
                    summaryPlayer.setBalance(result.getBalance());
                }
            }
            summaryPlayer.setCards(gamePlayer.getCards()); //未出完的牌
            summary.getPlayers().add(summaryPlayer);
        }
        if (dizhuSummaryPlayer != null) {
            dizhuSummaryPlayer.setScore(dizhuScore);
            if (dizhuWin) {
                result = PvaTools.getGoldCoins().income(dizhuPlayerUser, PVAInComeActionEnum.WIN.toString(), dizhuScore);
            } else {
                result = PvaTools.getGoldCoins().consume(dizhuPlayerUser, PVAConsumeActionEnum.LOST.toString(), dizhuScore);
            }
            dizhuSummaryPlayer.setBalance(result.getBalance());
        }
        summary.setGameRoomOver(gameRoomOver);    //有玩家破产，房间解散
        /**
         * 上面的 Player的 金币变更需要保持 数据库的日志记录 , 机器人的 金币扣完了就出局了
         */
        return summary;
    }

    /**
     * 找到玩家数据
     * @param userid
     * @return
     */
    public ClientSession getPlayerClient(List<ClientSession> clientSessionList,String userid){
        ClientSession temp = null;
        for(ClientSession user : clientSessionList){
            if(user.getId().equals(userid)){
                temp = user ; break ;
            }
        }
        return temp ;
    }
}
