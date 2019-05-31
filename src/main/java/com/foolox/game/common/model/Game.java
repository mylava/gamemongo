package com.foolox.game.common.model;

import com.foolox.game.common.repo.domain.GameRoom;
import com.foolox.game.core.engin.game.state.GameEvent;
import com.foolox.game.core.statemachine.impl.FooloxMachineHandler;
import com.foolox.game.core.statemachine.impl.MessageBuilder;

/**
 * comment: 游戏状态
 *
 * @author: lipengfei
 * @date: 29/05/2019
 */
public class Game {
    private final FooloxMachineHandler handler;

    public Game(FooloxMachineHandler handler) {
        this.handler = handler;
    }

    public void change(GameRoom gameRoom, String event) {
        change(gameRoom, event, 0);
    }

    public void change(GameRoom gameRoom, String event, int interval) {
        handler.handleEventWithState(MessageBuilder.withPayload(event)
                .setHeader("room", gameRoom.getId()).setHeader("interval", interval).build(), event);
    }

    public void change(GameEvent gameEvent) {
        change(gameEvent, 0);
    }

    public void change(GameEvent gameEvent, int interval) {
        handler.handleEventWithState(MessageBuilder.withPayload(gameEvent.getEventType().toString())
                        .setHeader("room", gameEvent.getRoomid()).setHeader("interval", interval).build(),
                gameEvent.getEventType().toString());
    }
}
