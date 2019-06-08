package com.foolox.game.core.logic.action;

import com.foolox.game.common.repo.domain.AiConfig;
import com.foolox.game.common.repo.domain.GameRoom;
import com.foolox.game.common.util.FooloxGameTaskUtil;
import com.foolox.game.common.util.FooloxUtils;
import com.foolox.game.core.logic.task.CreateAITask;
import com.foolox.game.core.statemachine.action.Action;
import com.foolox.game.core.statemachine.config.Transition;
import com.foolox.game.core.statemachine.message.Message;
import org.apache.commons.lang3.StringUtils;

/**
 * comment:创建房间的人，房卡模式下的 房主， 大厅模式下的首个进入房间的人
 *
 * @author: lipengfei
 * @date: 30/05/2019
 */
public class EnterAction implements Action {
    /**
     * 进入房间后开启 5秒计时模式，计时结束后未撮合玩家成功 的，召唤机器人，
     * 撮合成功的，立即开启游戏
     */
    @Override
    public void execute(Message message, Transition transition) {
        String roomId = (String) message.getMessageHeaders().getHeaders().get("roomId");
        if (!StringUtils.isBlank(roomId)) {
            GameRoom gameRoom = FooloxUtils.getRoomById(roomId);
            if (gameRoom != null) {
                if (!gameRoom.isCardroom()) {
                    AiConfig aiConfig = FooloxUtils.getAiConfigByPlaywayId(gameRoom.getPlaywayId());
                    if (aiConfig.getEnableai()) {
                        FooloxGameTaskUtil.getExpireCache().put(gameRoom.getId(), new CreateAITask(aiConfig.getWaittime(), gameRoom));
                    }
                }
                /**
                 * 更新状态
                 */
                gameRoom.setStatus(transition.getTarget());
                FooloxUtils.setRoomById(gameRoom.getId(), gameRoom);
            }
        }
    }
}
