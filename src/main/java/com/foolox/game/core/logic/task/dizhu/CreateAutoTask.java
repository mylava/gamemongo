package com.foolox.game.core.logic.task.dizhu;

import com.foolox.game.common.repo.domain.ClientSession;
import com.foolox.game.common.repo.domain.GameRoom;
import com.foolox.game.common.util.FooloxUtils;
import com.foolox.game.constants.Command;
import com.foolox.game.constants.PlayerType;
import com.foolox.game.core.engin.game.ActionTaskUtils;
import com.foolox.game.core.engin.game.FooloxGameTask;
import com.foolox.game.core.engin.game.GameBoard;
import com.foolox.game.core.engin.game.event.DiZhuBoard;
import com.foolox.game.core.engin.game.event.GamePlayer;
import com.foolox.game.core.engin.game.state.PlayerEvent;
import com.foolox.game.core.engin.game.task.AbstractTask;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;

import java.util.List;

/**
 * comment:
 *
 * @author: lipengfei
 * @date: 31/05/2019
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class CreateAutoTask extends AbstractTask implements FooloxGameTask {

    private long timer;
    private GameRoom gameRoom = null;

    @Override
    public long getCacheExpiryTime() {
        return System.currentTimeMillis() + timer * 1000;    //5秒后执行
    }

    @Override
    public void execute() {
        DiZhuBoard board = FooloxUtils.getBoardByRoomId(gameRoom.getId(), DiZhuBoard.class);
        GamePlayer randomCardPlayer = null, catchPlayer = null;
        int index = 0;
        if (board != null) {
            /**
             * 抢地主，首个抢地主的人 在发牌的时候已经生成
             */
            for (int i = 0; i < board.getGamePlayers().length; i++) {
                GamePlayer player = board.getGamePlayers()[i];
                if (player.isRandomcard()) {
                    randomCardPlayer = player;
                    index = i;
                    break;
                }
            }
            if (randomCardPlayer.isDocatch()) {
                catchPlayer = board.next(index);
            } else {
                catchPlayer = randomCardPlayer;
            }
            /**
             * 第二次抢地主条件：
             * 	1、抓到随机牌的人
             *  2、已经叫过一次地主了
             *  3、其他有人抢了地主
             *  4、首次地主选择 了 叫地主
             */
            if (catchPlayer == null) {
                /**
                 * 抓到随机牌的人如果选择了不叫地主，则二次选择的玩家是下一个 抢了地主的玩家
                 */
                if (randomCardPlayer.isRecatch() == false && !board.getBanker().equals(randomCardPlayer.getPlayuserId()) && randomCardPlayer.isAccept()) {
                    catchPlayer = randomCardPlayer;    //起到地主牌的人第二次抢地主 ， 抢完就结束了
                    randomCardPlayer.setRecatch(true);
                } else if (board.getBanker() == null) {
                    //流局了
                } else if (!StringUtils.isBlank(board.getBanker()) && randomCardPlayer.isAccept() == false && randomCardPlayer.isDocatch()) {
                    //下一个抢地主的人
                    GamePlayer temp = board.nextPlayer(index);
                    if (temp.isAccept() && temp.isRecatch() == false) {
                        catchPlayer = temp;
                        temp.setRecatch(true);
                    }

                }
            }
        }
        /**
         * 地主抢完了即可进入玩牌的流程了，否则，一直发送 AUTO事件，进行抢地主
         */
        if (catchPlayer != null) {
            catchPlayer.setDocatch(true);//抢过了
//			board.setBanker(catchPlayer.getPlayuser());	//玩家 点击 抢地主按钮后 赋值
            sendEvent(Command.CATCH, new GameBoard(catchPlayer.getPlayuserId(), board.isDocatch(), catchPlayer.isAccept(), board.getRatio()), gameRoom);

            boolean isNormal = true;
            List<ClientSession> clientSessionList = FooloxUtils.getRoomClientSessionList(gameRoom.getId());
            for (ClientSession clientSession : clientSessionList) {
                if (catchPlayer.getPlayuserId().equals(clientSession.getId())) {
                    if (clientSession.getPlayerType() != PlayerType.NORMAL) {
                        //AI或托管，自动抢地主，后台配置 自动抢地主的触发时间，或者 抢还是不抢， 无配置的情况下，默认的是抢地主
                        isNormal = false;
                        /**
                         * 根据 配置参数获取是否选择叫地主
                         */
                        board = ActionTaskUtils.doCatch(board, catchPlayer, true);
                        break;
                    }
                }
            }

            if (isNormal) {    //真人
                super.getGame(gameRoom.getPlaywayId()).change(gameRoom, PlayerEvent.AUTO, 17);    //通知状态机 , 此处应由状态机处理异步执行
            } else {            //AI或托管
                sendEvent(Command.CATCH_RESULT, new GameBoard(catchPlayer.getPlayuserId(), catchPlayer.isAccept(), catchPlayer.isAccept(), board.getRatio()), gameRoom);
                super.getGame(gameRoom.getPlaywayId()).change(gameRoom, PlayerEvent.AUTO, 2);    //通知状态机 , 此处应由状态机处理异步执行
                board.setDocatch(true);    //变成抢地主
            }
            //更新board状态到缓存
            FooloxUtils.setBoardByRoomId(gameRoom.getId(), board);
        } else {
            //开始打牌，地主的人是最后一个抢了地主的人
            super.getGame(gameRoom.getPlaywayId()).change(gameRoom, PlayerEvent.RAISEHANDS);    //通知状态机, 全部都抢过地主了,把底牌发给 最后一个抢到地主的人
        }
    }

}
