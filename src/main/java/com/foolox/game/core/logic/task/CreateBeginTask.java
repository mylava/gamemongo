package com.foolox.game.core.logic.task;

import com.foolox.game.common.repo.domain.ClientSession;
import com.foolox.game.common.repo.domain.GameRoom;
import com.foolox.game.common.util.FooloxUtils;
import com.foolox.game.common.util.GameUtils;
import com.foolox.game.constants.Command;
import com.foolox.game.constants.PlayerGameStatus;
import com.foolox.game.core.engin.game.ActionTaskUtils;
import com.foolox.game.core.engin.game.FooloxGameTask;
import com.foolox.game.core.engin.game.event.Banker;
import com.foolox.game.core.engin.game.event.Board;
import com.foolox.game.core.engin.game.event.UserBoard;
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
 * @date: 03/06/2019
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class CreateBeginTask extends AbstractTask implements FooloxGameTask {
    private long timer;
    private GameRoom gameRoom = null;

    @Override
    public long getCacheExpiryTime() {
        return System.currentTimeMillis() + timer * 1000;    //5秒后执行
    }

    @Override
    public void execute() {
        List<ClientSession> clientSessionList = FooloxUtils.getRoomClientSessionList(gameRoom.getId());
        /**
         *
         * 顺手 把牌发了，注：此处应根据 GameRoom的类型获取 发牌方式
         */
        boolean inroom = false;
        if (!StringUtils.isBlank(gameRoom.getLastwinner())) {
            for (ClientSession player : clientSessionList) {
                if (player.getUserId().equals(gameRoom.getLastwinner())) {
                    inroom = true;
                }
            }
        }
        if (!inroom) {
            gameRoom.setLastwinner(clientSessionList.get(0).getId());
        }
        /**
         * 通知所有玩家 新的庄
         */
        ActionTaskUtils.sendEvent(Command.BANKER, new Banker(gameRoom.getLastwinner()), gameRoom);

        Board board = GameUtils.playGame(clientSessionList, gameRoom, gameRoom.getLastwinner(), gameRoom.getCardsnum());
        FooloxUtils.setBoardByRoomId(gameRoom.getId(), board);
        for (ClientSession clientSession : clientSessionList) {
            clientSession.setPlayerGameStatus(PlayerGameStatus.PLAYING);
            /**
             * 更新状态到 PLAYING
             */

            if (FooloxUtils.getClientSessionById(clientSession.getUserId()) != null) {
                FooloxUtils.setClientSessionById(clientSession.getId(), clientSession);
            }
            /**
             * 每个人收到的 牌面不同，所以不用 ROOM发送广播消息，而是用 遍历房间里所有成员发送消息的方式
             */
            ActionTaskUtils.sendEvent(clientSession.getUserId(), new UserBoard(board, clientSession.getUserId(), Command.PLAY));
        }
        FooloxUtils.setRoomById(gameRoom.getId(), gameRoom);

        /**
         * 发送一个 Begin 事件
         */
        super.getGame(gameRoom.getPlaywayId()).change(gameRoom, PlayerEvent.AUTO, 2);    //通知状态机 , 此处应由状态机处理异步执行
    }
}
