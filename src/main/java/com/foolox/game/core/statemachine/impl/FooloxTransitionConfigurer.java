package com.foolox.game.core.statemachine.impl;

import com.foolox.game.core.statemachine.config.ExternalTransition;
import com.foolox.game.core.statemachine.config.StateMachineTransitionConfigurer;

import java.util.HashMap;
import java.util.Map;

/**
 * comment: 状态转换配置实现类
 *
 * @author: lipengfei
 * @date: 30/05/2019
 */
public class FooloxTransitionConfigurer<T,S> implements StateMachineTransitionConfigurer<T,S> {

    private Map<S, FooloxExtentionTransition<T, S>> transitions = new HashMap<S,FooloxExtentionTransition<T,S>>();

    @Override
    public ExternalTransition<T, S> withExternal() throws Exception {
        return new FooloxExtentionTransition<T, S>(this);
    }

    @Override
    public void apply(FooloxExtentionTransition<T, S> transition) {
        transitions.put(transition.getEvent(), transition);
    }

    @Override
    public FooloxExtentionTransition<T, S> transition(T event) {
        return transitions.get(event);
    }
}
