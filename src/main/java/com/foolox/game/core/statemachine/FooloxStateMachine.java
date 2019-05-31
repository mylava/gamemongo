package com.foolox.game.core.statemachine;

import com.foolox.game.core.statemachine.config.StateConfigurer;
import com.foolox.game.core.statemachine.config.StateMachineTransitionConfigurer;
import com.foolox.game.core.statemachine.impl.FooloxStateConfigurer;
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
    private StateConfigurer<String,String> config = new FooloxStateConfigurer<>();
    private StateMachineTransitionConfigurer<T,S> transitions = new FooloxTransitionConfigurer<T,S>() ;

    public StateConfigurer<String,String> getConfig() {
        return config;
    }
    public void setConfig(StateConfigurer<String,String> config) {
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
