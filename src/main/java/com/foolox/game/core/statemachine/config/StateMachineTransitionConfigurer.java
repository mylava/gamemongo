package com.foolox.game.core.statemachine.config;

import com.foolox.game.core.statemachine.impl.FooloxExtentionTransition;

/**
 * comment: 状态机转换配置接口
 *
 * @author: lipengfei
 * @date: 29/05/2019
 */
public interface StateMachineTransitionConfigurer<S, E> {
    /**
     * Gets a configurer for external transition.
     *
     * @return {@link ExternalTransition} for chaining
     * @throws Exception if configuration error happens
     */
    ExternalTransition<S, E> withExternal() throws Exception;

    void apply(FooloxExtentionTransition<S, E> transition);

    FooloxExtentionTransition<S,E> transition(S event) ;
}
