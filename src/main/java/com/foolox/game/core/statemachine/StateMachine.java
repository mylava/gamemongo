package com.foolox.game.core.statemachine;

import com.foolox.game.core.engin.game.state.PlayerEvent;
import com.foolox.game.core.statemachine.config.State;
import com.foolox.game.core.statemachine.config.Transition;
import lombok.Data;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * comment:
 *
 * @author: lipengfei
 * @date: 03/06/2019
 */
@Data
public class StateMachine {
    private State stateContext = new State();
    private Map<PlayerEvent,Transition> transitionContext = new LinkedHashMap<>();

    public StateMachine addTransition(Transition transition) {
        transitionContext.put(transition.getEvent(),transition);
        return this;
    }
}
