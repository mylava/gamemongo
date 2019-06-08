package com.foolox.game.core.statemachine;

import com.foolox.game.common.repo.domain.GameRoom;
import com.foolox.game.core.engin.game.state.GameEvent;
import com.foolox.game.core.engin.game.state.PlayerEvent;
import com.foolox.game.core.statemachine.handler.StateMachineHandler;
import com.foolox.game.core.statemachine.message.MessageBuilder;

/**
 * comment: 游戏状态
 *
 * @author: lipengfei
 * @date: 29/05/2019
 */
public class Game {
    private final StateMachineHandler handler;

    public Game(StateMachineHandler handler) {
        this.handler = handler;
    }

    public void change(GameRoom gameRoom, PlayerEvent event) {
        change(gameRoom, event, 0);
    }

    public void change(GameRoom gameRoom, PlayerEvent event, int interval) {
        handler.handleEventWithState(MessageBuilder.withPayload(event)
                .setHeader("roomId", gameRoom.getId()).setHeader("interval", interval).build(), event);
    }

    public void change(GameEvent gameEvent) {
        change(gameEvent, 0);
    }

    public void change(GameEvent gameEvent, int interval) {
        handler.handleEventWithState(MessageBuilder.
                        withPayload(gameEvent.getEventType())
                        .setHeader("roomId", gameEvent.getRoomid())
                        .setHeader("interval", interval)
                        .build(),
                gameEvent.getEventType());
    }
}
