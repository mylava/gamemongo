package com.foolox.game.core.logic.action.dizhu;

import com.foolox.game.common.repo.domain.GameRoom;
import com.foolox.game.common.util.FooloxGameTaskUtil;
import com.foolox.game.common.util.FooloxUtils;
import com.foolox.game.core.engin.game.event.DiZhuBoard;
import com.foolox.game.core.logic.task.dizhu.CreatePlayCardsTask;
import com.foolox.game.core.statemachine.action.Action;
import com.foolox.game.core.statemachine.config.Transition;
import com.foolox.game.core.statemachine.message.Message;
import org.apache.commons.lang3.StringUtils;

/**
 * comment: 打牌动作
 *
 * @author: lipengfei
 * @date: 30/05/2019
 */
public class DizhuPlayCardsAction implements Action {

    @Override
    public void execute(Message message, Transition transition) {
        String roomId = (String) message.getMessageHeaders().getHeaders().get("roomId");
        if (!StringUtils.isBlank(roomId)) {
            GameRoom gameRoom = FooloxUtils.getRoomById(roomId);
            if (gameRoom != null) {
                DiZhuBoard board = FooloxUtils.getBoardByRoomId(gameRoom.getId(), DiZhuBoard.class);
                int interval = (int) message.getMessageHeaders().getHeaders().get("interval");
                String nextPlayer = board.getBanker();
                if (!StringUtils.isBlank(board.getNextplayer().getNextplayer())) {
                    nextPlayer = board.getNextplayer().getNextplayer();
                }
                FooloxGameTaskUtil.getExpireCache().put(gameRoom.getRoomid(), new CreatePlayCardsTask(interval, gameRoom, nextPlayer));
            }
        }
    }
}
