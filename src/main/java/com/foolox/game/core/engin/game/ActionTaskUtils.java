package com.foolox.game.core.engin.game;

import com.foolox.game.common.repo.domain.ClientSession;
import com.foolox.game.common.repo.domain.GameRoom;
import com.foolox.game.common.util.FooloxUtils;
import com.foolox.game.common.util.client.FooloxClientContext;
import com.foolox.game.common.util.redis.PlayerPrefix;
import com.foolox.game.common.util.redis.RedisService;
import com.foolox.game.constants.Command;
import com.foolox.game.constants.PlayerStatus;
import com.foolox.game.core.FooloxDataContext;
import com.foolox.game.core.engin.game.event.Board;
import com.foolox.game.core.engin.game.event.DiZhuBoard;
import com.foolox.game.core.engin.game.event.GamePlayer;
import com.foolox.game.core.engin.game.event.RoomPlayers;
import com.foolox.game.core.engin.game.task.AbstractTask;
import com.foolox.game.core.logic.dizhu.CardsTypeEnum;
import com.foolox.game.core.logic.dizhu.task.CreateAutoTask;
import com.foolox.game.core.server.FooloxClient;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * comment: 游戏中动作通知的工具类
 *
 * @author: lipengfei
 * @date: 29/05/2019
 */
public class ActionTaskUtils {
    private static RedisService redisService = FooloxDataContext.getApplicationContext().getBean(RedisService.class);

    /**
     * 发送消息给房间中的每个玩家
     *
     * @param command
     * @param message
     * @param gameRoom
     */
    public static void sendEvent(Command command, Message message, GameRoom gameRoom) {
        message.setCommand(command);
        List<ClientSession> clientSessionList = FooloxUtils.getRoomClientSessionList(gameRoom.getId());
        //循环给房间的每个人发消息
        for (ClientSession session : clientSessionList) {
            FooloxClient fooloxClient = FooloxClientContext.getFooloxClientCache().getClient(session.getId());
            if (fooloxClient != null && ifOnline(session.getId())) {
                fooloxClient.sendEvent(FooloxDataContext.FOOLOX_MESSAGE_EVENT, message);
            }
        }
    }

    /**
     * 发送消息给 玩家
     *
     * @param fooloxClient
     * @param gameRoom
     */
    public static void sendPlayers(FooloxClient fooloxClient, GameRoom gameRoom) {
        if (ifOnline(fooloxClient.getUserid())) {
            List<ClientSession> clientSessionList = FooloxUtils.getRoomClientSessionList(gameRoom.getId());
            fooloxClient.sendEvent(FooloxDataContext.FOOLOX_MESSAGE_EVENT, new RoomPlayers(gameRoom.getMaxPlayerNum(), clientSessionList, Command.GET_ROOM_PLAYERS));
        }
    }

    /**
     * 检查玩家是否在线
     *
     * @param userId
     * @return
     */
    public static boolean ifOnline(String userId) {
        ClientSession clientSession = FooloxUtils.getClientSessionById(userId);
        return clientSession != null && PlayerStatus.OFFLINE != clientSession.getPlayerStatus() && PlayerStatus.LEAVE != clientSession.getPlayerStatus();
    }


    /**
     * --------------- ---------------
     * 临时放这里，重构的时候 放到 游戏类型的 实现类里
     * --------------- ---------------
     */
    /**
     * 牌型识别
     *
     * @param cards
     * @return
     */
    public static CardType identification(byte[] cards) {
        CardType cardTypeBean = new CardType();
        Map<Integer, Integer> types = new HashMap<Integer, Integer>();
        //最大牌的张数
        int max = -1;
        //最大牌的点数
        int maxcard = -1, cardtype = 0, mincard = -1, min = 100;
        for (int i = 0; i < cards.length; i++) {
            int card = cards[i] / 4;
            //计算同一点数牌的张数
            if (types.get(card) == null) {
                types.put(card, 1);
            } else {
                types.put(card, types.get(card) + 1);
            }
            //找出最大牌的张数、点数
            if (types.get(card) > max) {
                max = types.get(card);
                maxcard = card;
            }
            //等于最大牌张数（即这张牌最大牌）
            if (types.get(card) == max) {
                if (mincard < 0 || mincard > card) {
                    mincard = card;
                }
            }

            if (cards[i] > cardTypeBean.getMaxcardvalue()) {
                cardTypeBean.setMaxcardvalue(cards[i]);
            }
        }

        Iterator<Integer> iterator = types.keySet().iterator();
        while (iterator.hasNext()) {
            Integer key = iterator.next();
            if (types.get(key) < min) {
                min = types.get(key);
            }
        }

        cardTypeBean.setCardnum(max);
        cardTypeBean.setMincard(mincard);
        cardTypeBean.setTypesize(types.size());
        cardTypeBean.setMaxcard(maxcard);


        switch (types.size()) {
            case 1:
                switch (max) {
                    case 1:
                        cardtype = CardsTypeEnum.ONE.getType();
                        break;        //单张
                    case 2:
                        if (mincard == 13) {
                            cardtype = CardsTypeEnum.ELEVEN.getType();
                        } else {
                            cardtype = CardsTypeEnum.TWO.getType();
                        }
                        break;        //一对
                    case 3:
                        cardtype = CardsTypeEnum.THREE.getType();
                        break;        //三张
                    case 4:
                        cardtype = CardsTypeEnum.TEN.getType();
                        break;        //炸弹
                    default:
                        break;
                }
                break;
            case 2:
                switch (max) {
                    case 3:
                        if (min == 1) {//三带一
                            cardtype = CardsTypeEnum.FOUR.getType();
                        } else if (min == 2) {//三带一对
                            cardtype = CardsTypeEnum.FORMTWO.getType();
                        } else if (min == 3) {//飞机不带
                            cardtype = CardsTypeEnum.SEVEN.getType();
                        }
                        break;
                    case 4:
                        cardtype = CardsTypeEnum.NINE.getType();
                        break;    //四带一对
                    default:
                        break;
                }
                break;
            case 3:
                switch (max) {
                    case 1:
                        ;
                        break;    //无牌型
                    case 2:
                        if (cards.length == 6 && isHave(types, mincard)) {
                            cardtype = CardsTypeEnum.SIX.getType();
                        }
                        break;        //3连对
                    case 3:
                        if (isHave(types, mincard) && min == max) {
                            cardtype = CardsTypeEnum.SEVEN.getType();
                        }
                        break;        //三顺
                    case 4:
                        if (cards.length == 6 || cards.length == 8) {
                            cardtype = CardsTypeEnum.NINE.getType();
                        }
                        break;        //四带二
                    default:
                        break;
                }
                break;
            case 4:
                switch (max) {
                    case 1:
                        ;
                        break;        //无牌型
                    case 2:
                        if (cards.length == 8 && isHave(types, mincard)) {
                            cardtype = CardsTypeEnum.SIX.getType();
                        }
                        break;        //4连对
                    case 3:
                        if (isHave(types, mincard)) {
                            if (cards.length == 8) {
                                cardtype = CardsTypeEnum.EIGHT.getType();
                            } else if (cards.length == 10) {
                                cardtype = CardsTypeEnum.EIGHTONE.getType();
                            }
                        }
                        break;        //飞机
                    default:
                        break;
                }
                break;
            case 5:
                switch (max) {
                    case 1:
                        if (isHave(types, mincard) && max == min) {
                            cardtype = CardsTypeEnum.FIVE.getType();
                        }
                        break;        //连子
                    case 2:
                        if (cards.length == 10 && isHave(types, mincard)) {
                            cardtype = CardsTypeEnum.SIX.getType();
                        }
                        break;        //5连对
                    case 3:
                        if (isHave(types, mincard) && max == min) {
                            cardtype = CardsTypeEnum.SEVEN.getType();
                        }
                        break;        //5飞机
                    default:
                        break;
                }
                break;
            case 6:
                switch (max) {
                    case 1:
                        if (isHave(types, mincard) && max == min) {
                            cardtype = CardsTypeEnum.FIVE.getType();
                        }
                        break;        //连子
                    case 2:
                        if (isHave(types, mincard) && max == min) {
                            cardtype = CardsTypeEnum.SIX.getType();
                        }
                        break;        //6连对
                    case 3:
                        if (isHave(types, mincard) && max == min) {
                            cardtype = CardsTypeEnum.SEVEN.getType();
                        }
                        break;        //6飞机
                    default:
                        break;
                }
                break;
            default:
                switch (max) {
                    case 1:
                        if (isHave(types, mincard)) {
                            cardtype = CardsTypeEnum.FIVE.getType();
                        }
                        break;        //连子
                    case 2:
                        if (isHave(types, mincard) && max == min) {
                            cardtype = CardsTypeEnum.SIX.getType();
                        }
                        break;        //连对
                    default:
                        break;
                }
                break;
        }
        cardTypeBean.setCardtype(cardtype);
        cardTypeBean.setKing(cardtype == CardsTypeEnum.ELEVEN.getType());
        cardTypeBean.setBomb(cardtype == CardsTypeEnum.TEN.getType());
        return cardTypeBean;
    }

    private static boolean isHave(Map<Integer, Integer> types, int mincard) {
        boolean ava = true;
        for (int i = mincard; i < (mincard + types.size()); i++) {
            if (types.get(i) == null) {
                ava = false;
            }
        }
        return ava;
    }

    /**
     * 分类
     *
     * @param cards
     * @return
     */
    public static Map<Integer, Integer> type(byte[] cards) {
        Map<Integer, Integer> types = new HashMap<Integer, Integer>();
        for (int i = 0; i < cards.length; i++) {
            int card = cards[i] / 4;
            if (types.get(card) == null) {
                types.put(card, 1);
            } else {
                types.put(card, types.get(card) + 1);
            }
        }
        return types;
    }

    /**
     * 校验当前出牌是否合规
     *
     * @param playCardType
     * @param lastCardType
     * @return
     */
    public static boolean allow(CardType playCardType, CardType lastCardType) {
        boolean allow = false;
        if (playCardType.isKing()) {    //王炸，无敌
            allow = true;
        } else if (playCardType.isBomb()) {
            if (lastCardType.isBomb()) { //都是炸弹
                if (playCardType.getMaxcard() > lastCardType.getMaxcard()) {
                    allow = true;
                }
            } else if (lastCardType.isKing()) {
                allow = false;
            } else {
                allow = true;
            }
        } else if (lastCardType.isBomb()) {    //最后一手牌是炸弹 ， 当前出牌不是炸弹
            allow = false;
        } else if (playCardType.getCardtype() == lastCardType.getCardtype() && playCardType.getCardtype() > 0 && lastCardType.getCardtype() > 0) {
            if (playCardType.getMaxcard() > lastCardType.getMaxcard()) {
                allow = true;
            } else if (playCardType.getMaxcardvalue() == 53) {
                allow = true;
            }
        }
        return allow;
    }

    /**
     * 临时放这里，重构的时候 放到 游戏类型的 实现类里
     *
     * @param board
     * @return
     */
    public static void doBomb(Board board, boolean add) {
        if (add) {    //抢了地主
            board.setRatio(board.getRatio() * 2);
        }
    }

    /**
     * 通过roomId 获取 Clientsession
     *
     * @param roomId
     * @param player
     * @return
     */
    public static ClientSession getClientSession(String roomId, String player) {
        ClientSession clientSession = null;
        List<ClientSession> clientSessionList = FooloxUtils.getRoomClientSessionList(roomId);
        for (ClientSession user : clientSessionList) {
            if (player.equals(user.getId())) {
                clientSession = user;
            }
        }
        return clientSession;
    }

    /**
     * 临时放这里，重构的时候 放到 游戏类型的 实现类里
     * 抢地主的时候，首个抢地主 不翻倍
     *
     * @param board
     * @param gamePlayer
     * @return
     */
    public static DiZhuBoard doCatch(DiZhuBoard board, GamePlayer gamePlayer, boolean result) {
        gamePlayer.setAccept(result); //抢地主
        gamePlayer.setDocatch(true);
        board.setDocatch(true);
        if (result) {    //抢了地主
            if (board.isAdded() == false) {
                board.setAdded(true);
            } else {
                board.setRatio(board.getRatio() * 2);
            }
            board.setBanker(gamePlayer.getPlayuserId());
        }
        return board;
    }

    public static AbstractTask createAutoTask(int times, GameRoom gameRoom) {
        return new CreateAutoTask(times, gameRoom, gameRoom.getOrgi());
    }
}