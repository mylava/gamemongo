package com.foolox.game.core.statemachine.config;

import com.foolox.game.constants.RoomStatus;
import com.foolox.game.core.engin.game.state.PlayerEvent;
import com.foolox.game.core.statemachine.action.Action;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * comment:
 *
 * @author: lipengfei
 * @date: 03/06/2019
 */
@Data
@NoArgsConstructor
public class Transition {

    private RoomStatus source = null;
    private RoomStatus target = null;
    private PlayerEvent event = null;
    private Action action = null, errorAction = null;

    public Transition source(RoomStatus source) {
        this.source = source;
        return this;
    }

    public Transition event(PlayerEvent event) {
        this.event = event;
        return this;
    }

    public Transition action(Action action) {
        this.action = action;
        return this;
    }

    public Transition action(Action action,
                             Action error) {
        this.action = action;
        this.errorAction = error;
        return null;
    }

    public Transition target(RoomStatus target) {
        this.target = target;
        return this;
    }

}
