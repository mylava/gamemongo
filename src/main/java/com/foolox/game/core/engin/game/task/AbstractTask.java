package com.foolox.game.core.engin.game.task;

import com.foolox.game.common.model.Game;
import com.foolox.game.common.repo.domain.GamePlayway;
import com.foolox.game.common.repo.domain.GameRoom;
import com.foolox.game.common.util.GameUtils;
import com.foolox.game.constants.Command;
import com.foolox.game.core.engin.game.ActionTaskUtils;
import com.foolox.game.core.engin.game.Message;
import org.cache2k.expiry.ValueWithExpiryTime;

/**
 * comment:
 *
 * @author: lipengfei
 * @date: 31/05/2019
 */
public abstract class AbstractTask implements ValueWithExpiryTime {
    /**
     * 根据玩法，找到对应的状态机
     *
     * @param playway
     * @return
     */
    public Game getGame(String playway) {
        return GameUtils.getGame(playway);
    }

    public void sendEvent(Command command, Message message, GameRoom gameRoom) {
        ActionTaskUtils.sendEvent(command, message, gameRoom);
    }

    public Object json(Object data) {
        return data;
    }

    /**
     * 根据当前 ROOM的 玩法， 确定下一步的流程
     *
     * @param playway
     * @param currentStatus
     * @return
     */
    public String getNextEvent(GamePlayway playway, String currentStatus) {
        return "";
    }
}
