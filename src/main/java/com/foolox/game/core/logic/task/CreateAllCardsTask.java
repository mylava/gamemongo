package com.foolox.game.core.logic.task;

import com.foolox.game.common.model.Summary;
import com.foolox.game.common.repo.domain.ClientSession;
import com.foolox.game.common.repo.domain.GamePlayway;
import com.foolox.game.common.repo.domain.GameRoom;
import com.foolox.game.common.util.FooloxUtils;
import com.foolox.game.constants.Command;
import com.foolox.game.constants.PlayerGameStatus;
import com.foolox.game.constants.PlayerType;
import com.foolox.game.core.FooloxDataContext;
import com.foolox.game.core.engin.game.ActionTaskUtils;
import com.foolox.game.core.engin.game.FooloxGameTask;
import com.foolox.game.core.engin.game.event.Board;
import com.foolox.game.core.engin.game.event.GamePlayer;
import com.foolox.game.core.engin.game.task.AbstractTask;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * comment:
 *
 * @author: lipengfei
 * @date: 04/06/2019
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class CreateAllCardsTask extends AbstractTask implements FooloxGameTask {
    private long timer;
    private GameRoom gameRoom;

    @Override
    public long getCacheExpiryTime() {
        return System.currentTimeMillis() + timer * 1000;    //5秒后执行
    }

    @Override
    public void execute() {
        Board board = FooloxUtils.getBoardByRoomId(gameRoom.getId(), Board.class);
        board.setFinished(true);
        GamePlayway gamePlayWay = FooloxUtils.getGamePlaywayById(gameRoom.getPlaywayId());
        boolean gameOver = false;
        if (gamePlayWay != null) {
            /**
             * 结算信息 ， 更新 玩家信息
             */
            Summary summary = board.summary(board, gameRoom, gamePlayWay);
            sendEvent(Command.ALLCARDS, summary, gameRoom);    //通知所有客户端结束牌局，进入结算
            if (summary.isGameRoomOver()) {
                gameOver = true;
            }
        }
        for (GamePlayer player : board.getGamePlayers()) {
            ClientSession session = FooloxUtils.getClientSessionById(player.getPlayuserId());
            if (session != null) {
                if (session.getPlayerType()!=PlayerType.AI) {
                    session.setPlayerGameStatus(PlayerGameStatus.NOTREADY);
                    FooloxUtils.setClientSessionById(session.getUserId(), session);

                    if (session.getPlayerType()==PlayerType.NORMAL) {
                        /**
                         * 历史遗留的问题，CacheHelper.getApiUserCacheBean()获取的是真实玩家的 数据，
                         * 包括玩家的状态，CacheHelper.getGamePlayerCacheBean()存放的是包含机器人玩家的数据
                         */
                        session = FooloxUtils.getClientSessionById(player.getPlayuserId());
                        session.setRoomready(false);
                        FooloxUtils.setClientSessionById(session.getUserId(), session);
                    } else if (session.getPlayerType()==PlayerType.LEAVE || session.getPlayerType()==PlayerType.OFFLINE) {
                        //STAY 根据配置参数决定
                        /**
                         * 离线和托管玩家，离开房间以后，牌局结束时从当前房间清理出去
                         */
                        ActionTaskUtils.updatePlayerClientStatus(session, session.getPlayerType());
                    }
                }
            }
        }

        if (gameOver) {
            //删除房间内所有玩家
            FooloxUtils.delRoomClientSessionList(gameRoom.getId());
            for (GamePlayer player : board.getGamePlayers()) {
                //删除玩家与房间缓存
                FooloxUtils.delRoomIdByUserId(player.getPlayuserId());
            }
            if (!gameRoom.isCardroom()) { //房卡模式，清理掉房卡资源
                /**
                 * 重新加入房间资源到 队列
                 */
                FooloxUtils.addRoom2Queue(gameRoom.getId(), gameRoom);
            }
        }

        FooloxDataContext.getGameEngine().finished(gameRoom.getId());
    }
}
