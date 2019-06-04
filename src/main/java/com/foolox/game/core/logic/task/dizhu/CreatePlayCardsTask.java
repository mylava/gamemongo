package com.foolox.game.core.logic.task.dizhu;

import com.foolox.game.common.repo.domain.GameRoom;
import com.foolox.game.core.FooloxDataContext;
import com.foolox.game.core.engin.game.FooloxGameTask;
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
public class CreatePlayCardsTask extends AbstractTask implements FooloxGameTask {
    private long timer;
    private GameRoom gameRoom;
    private String player;

    @Override
    public long getCacheExpiryTime() {
        return System.currentTimeMillis() + timer * 1000;    //5秒后执行
    }

    @Override
    public void execute() {
        /**
         * 合并代码，玩家 出牌超时处理和 玩家出牌统一使用一处代码
         */
        FooloxDataContext.getGameEngine().takeCardsRequest(this.gameRoom.getId(), this.player, true, null);

    }
}
