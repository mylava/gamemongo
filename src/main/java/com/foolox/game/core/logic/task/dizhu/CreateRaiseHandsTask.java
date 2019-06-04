package com.foolox.game.core.logic.task.dizhu;

import com.foolox.game.common.repo.domain.ClientSession;
import com.foolox.game.common.repo.domain.GameRoom;
import com.foolox.game.common.util.FooloxUtils;
import com.foolox.game.common.util.GameUtils;
import com.foolox.game.constants.Command;
import com.foolox.game.constants.PlayerType;
import com.foolox.game.core.engin.game.ActionTaskUtils;
import com.foolox.game.core.engin.game.FooloxGameTask;
import com.foolox.game.core.engin.game.GameBoard;
import com.foolox.game.core.engin.game.event.DiZhuBoard;
import com.foolox.game.core.engin.game.event.GamePlayer;
import com.foolox.game.core.engin.game.event.NextPlayer;
import com.foolox.game.core.engin.game.state.PlayerEvent;
import com.foolox.game.core.engin.game.task.AbstractTask;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.ArrayUtils;

import java.util.Arrays;

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
public class CreateRaiseHandsTask extends AbstractTask implements FooloxGameTask {
    private long timer;
    private GameRoom gameRoom;

    @Override
    public long getCacheExpiryTime() {
        return System.currentTimeMillis() + timer * 1000;    //5秒后执行
    }

    @Override
    public void execute() {
        DiZhuBoard board = FooloxUtils.getBoardByRoomId(gameRoom.getId(), DiZhuBoard.class);
        GamePlayer lastHandsPlayer = null;
        for (GamePlayer player : board.getGamePlayers()) {
            if (player.getPlayuserId().equals(board.getBanker())) {//抢到地主的人
                byte[] lastHands = board.pollLastHands();
                board.setLasthands(lastHands);

                board.setNextplayer(new NextPlayer(player.getPlayuserId(), false));
                player.setCards(ArrayUtils.addAll(player.getCards(), lastHands));//翻底牌
                Arrays.sort(player.getCards());                                      //重新排序
                player.setCards(GameUtils.reverseCards(player.getCards()));          //从大到小 倒序
                lastHandsPlayer = player;
                break;
            }
        }
        /**
         * 计算底牌倍率
         */
        board.setRatio(board.getRatio() * board.calcRatio());

        /**
         * 发送一个通知，翻底牌消息
         */
        sendEvent(Command.LAST_HANDS, new GameBoard(lastHandsPlayer.getPlayuserId(), board.getLasthands(), board.getRatio()), gameRoom);

        /**
         * 更新牌局状态
         */
        FooloxUtils.setBoardByRoomId(gameRoom.getId(), board);
        /**
         * 发送一个 开始打牌的事件 ， 判断当前出牌人是 玩家还是 AI，如果是 AI，则默认 1秒时间，如果是玩家，则超时时间是25秒
         */
        ClientSession clientSession = ActionTaskUtils.getOneClientSessionFromRoom(gameRoom.getId(), lastHandsPlayer.getPlayuserId());

        if (PlayerType.NORMAL==clientSession.getPlayerType()) {
            super.getGame(gameRoom.getPlaywayId()).change(gameRoom, PlayerEvent.PLAYCARDS, 25); //应该从 游戏后台配置参数中获取
        } else {
            super.getGame(gameRoom.getPlaywayId()).change(gameRoom, PlayerEvent.PLAYCARDS, 3); //应该从游戏后台配置参数中获取
        }
    }
}
