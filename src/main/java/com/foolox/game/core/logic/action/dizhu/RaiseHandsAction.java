package com.foolox.game.core.logic.action.dizhu;

import com.foolox.game.common.repo.domain.GameRoom;
import com.foolox.game.common.util.FooloxGameTaskUtil;
import com.foolox.game.common.util.FooloxUtils;
import com.foolox.game.core.logic.task.dizhu.CreateRaiseHandsTask;
import com.foolox.game.core.statemachine.action.Action;
import com.foolox.game.core.statemachine.config.Transition;
import com.foolox.game.core.statemachine.message.Message;
import org.apache.commons.lang3.StringUtils;

/**
 * comment: 把底牌发给地主
 *
 * @author: lipengfei
 * @date: 30/05/2019
 */
public class RaiseHandsAction implements Action {

    @Override
    public void execute(Message message, Transition transition) {
        String roomId = (String) message.getMessageHeaders().getHeaders().get("roomId");
        if (!StringUtils.isBlank(roomId)) {
            GameRoom gameRoom = FooloxUtils.getRoomById(roomId);
            if (gameRoom != null) {
                FooloxGameTaskUtil.getExpireCache().put(gameRoom.getRoomid(), new CreateRaiseHandsTask(0, gameRoom));
            }
        }
    }
}
