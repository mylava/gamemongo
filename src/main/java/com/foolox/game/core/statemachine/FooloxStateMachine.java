package com.foolox.game.core.statemachine;

import com.foolox.game.core.statemachine.config.State;
import com.foolox.game.core.statemachine.config.StateMachineTransitionConfigurer;
import com.foolox.game.core.statemachine.impl.FooloxState;
import com.foolox.game.core.statemachine.impl.FooloxTransitionConfigurer;

/**
 * comment: 瓜牛游戏状态机
 *
 * @author: lipengfei
 * @date: 29/05/2019
 */
public class FooloxStateMachine<T, S> {
    /**
     *
     */
    private State<String,String> config = new FooloxState<>();
    private StateMachineTransitionConfigurer<T,S> transitions = new FooloxTransitionConfigurer<T,S>() ;

    public State<String,String> getConfig() {
        return config;
    }
    public void setConfig(State<String,String> config) {
        this.config = config;
    }
    public StateMachineTransitionConfigurer<T,S> getTransitions() {
        return transitions;
    }
    public void setTransitions(
            StateMachineTransitionConfigurer<T,S> transitions) {
        this.transitions = transitions;
    }
}
