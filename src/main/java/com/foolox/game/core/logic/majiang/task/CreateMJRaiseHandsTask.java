package com.foolox.game.core.logic.majiang.task;

import com.foolox.game.common.repo.domain.ClientSession;
import com.foolox.game.common.repo.domain.GameRoom;
import com.foolox.game.common.util.FooloxUtils;
import com.foolox.game.common.util.GameUtils;
import com.foolox.game.constants.Command;
import com.foolox.game.constants.PlayerStatus;
import com.foolox.game.core.engin.game.ActionTaskUtils;
import com.foolox.game.core.engin.game.FooloxGameTask;
import com.foolox.game.core.engin.game.GameBoard;
import com.foolox.game.core.engin.game.event.Board;
import com.foolox.game.core.engin.game.event.GamePlayer;
import com.foolox.game.core.engin.game.event.NextPlayer;
import com.foolox.game.core.engin.game.event.SelectColor;
import com.foolox.game.core.engin.game.state.GameEventType;
import com.foolox.game.core.engin.game.task.AbstractTask;

/**
 * comment:
 *
 * @author: lipengfei
 * @date: 31/05/2019
 */
public class CreateMJRaiseHandsTask extends AbstractTask implements FooloxGameTask {
    private long timer;
    private GameRoom gameRoom = null;
    private String orgi;

    public CreateMJRaiseHandsTask(long timer, GameRoom gameRoom, String orgi) {
        super();
        this.timer = timer;
        this.gameRoom = gameRoom;
        this.orgi = orgi;
    }

    @Override
    public long getCacheExpiryTime() {
        return System.currentTimeMillis() + timer * 1000;    //5秒后执行
    }

    @Override
    public void execute() {
        /**
         *
         * 检查是否所有人都已经定缺，如果定缺完毕，则通知庄家开始出牌，如果有未完成定缺的，则自动选择
         */
        Board board = FooloxUtils.getBoardByRoomId(gameRoom.getId(), Board.class);
        //庄家
        GamePlayer banker = null;
        for (GamePlayer player : board.getGamePlayers()) {
            if (player.getPlayuserId().equals(board.getBanker())) {
                banker = player;
            }
            if (!player.isSelected()) {
                SelectColor color = new SelectColor(board.getBanker(), player.getPlayuserId());
                color.setColor(GameUtils.selectColor(player.getCards()));
                ActionTaskUtils.sendEvent(Command.SELECT_RESULT, color, gameRoom);
                player.setColor(color.getColor());
                player.setSelected(true);
                break;
            }
        }
        if (banker != null) {
            board.setNextplayer(new NextPlayer(board.getBanker(), false));
//            FooloxUtils.setBoardByRoomId(gameRoom.getId(), board);//更新缓存数据
            /**
             * 发送一个通知，告诉大家,开始出牌了
             */
            sendEvent(Command.LAST_HANDS, new GameBoard(banker.getPlayuserId(), board.getBanker(), board.getRatio()), gameRoom);

            /**
             * 更新牌局状态
             */
            FooloxUtils.setBoardByRoomId(gameRoom.getId(), board);
            /**
             * 发送一个 开始打牌的事件 ， 判断当前出牌人是 玩家还是 AI，如果是 AI，则默认 1秒时间，如果是玩家，则超时时间是25秒
             */
            ClientSession clientSession = ActionTaskUtils.getClientSession(gameRoom.getId(), banker.getPlayuserId());

            if (PlayerStatus.NORMAL == clientSession.getPlayerStatus()) {
                super.getGame(gameRoom.getPlaywayId()).change(gameRoom, GameEventType.PLAYCARDS.toString(), 8);    //应该从 游戏后台配置参数中获取
            } else {
                super.getGame(gameRoom.getPlaywayId()).change(gameRoom, GameEventType.PLAYCARDS.toString(), 3);    //应该从游戏后台配置参数中获取
            }
        }
    }
}
