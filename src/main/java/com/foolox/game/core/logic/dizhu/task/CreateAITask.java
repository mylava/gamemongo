package com.foolox.game.core.logic.dizhu.task;

import com.foolox.game.common.repo.domain.ClientSession;
import com.foolox.game.common.repo.domain.GameRoom;
import com.foolox.game.common.repo.domain.Player;
import com.foolox.game.common.util.FooloxUtils;
import com.foolox.game.common.util.GameUtils;
import com.foolox.game.constants.PlayerGameStatus;
import com.foolox.game.constants.PlayerStatus;
import com.foolox.game.core.engin.game.ActionTaskUtils;
import com.foolox.game.core.engin.game.FooloxGameTask;
import com.foolox.game.core.engin.game.task.AbstractTask;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

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
public class CreateAITask extends AbstractTask implements FooloxGameTask {
    private long timer;
    private GameRoom gameRoom = null;

    @Override
    public long getCacheExpiryTime() {
        return System.currentTimeMillis() + timer * 1000;    //5秒后执行
    }

    //执行生成AI
    @Override
    public void execute() {
        //将房间从撮合队列中移除
        FooloxUtils.removeRoomByRoomId(gameRoom.getPlaywayId(), gameRoom.getId());
        List<ClientSession> clientSessionList = FooloxUtils.getRoomClientSessionList(gameRoom.getId());
        /**
         * 清理 未就绪玩家
         */
        for (int i = 0; i < clientSessionList.size(); ) {
            ClientSession clientSession = clientSessionList.get(i);
            if (clientSession.getPlayerGameStatus() != PlayerGameStatus.READY){
                clientSessionList.remove(i);
                FooloxUtils.delClientSessionById(clientSession.getUserId());
                continue;
            }
            i++;
        }
        int aicount = gameRoom.getMaxPlayerNum() - clientSessionList.size();
        if (aicount > 0) {
            for (int i = 0; i < aicount; i++) {
                ClientSession AIclientSession = GameUtils.createAI(new Player(), gameRoom.getPlaywayId(), PlayerStatus.AI);
                AIclientSession.setPlayerindex(System.currentTimeMillis());    //按照加入房间的时间排序，有玩家离开后，重新发送玩家信息列表，重新座位
                AIclientSession.setRoomId(gameRoom.getId());
                AIclientSession.setRoomready(true);

                FooloxUtils.setClientSessionById(AIclientSession.getUserId(), AIclientSession);//将用户加入到 room
                clientSessionList.add(AIclientSession);
            }

            ActionTaskUtils.sendPlayersExclusiveAI(clientSessionList, gameRoom);

            /**
             * 发送一个 Enough 事件
             */
            ActionTaskUtils.roomReady(gameRoom, super.getGame(gameRoom.getPlaywayId()));
        }
    }
}
