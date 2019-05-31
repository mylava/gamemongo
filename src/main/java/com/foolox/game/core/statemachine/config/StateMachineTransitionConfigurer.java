package com.foolox.game.core.statemachine.config;

import com.foolox.game.core.statemachine.impl.FooloxExtentionTransitionConfigurer;

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
     * @return {@link ExternalTransitionConfigurer} for chaining
     * @throws Exception if configuration error happens
     */
    ExternalTransitionConfigurer<S, E> withExternal() throws Exception;

    void apply(FooloxExtentionTransitionConfigurer<S, E> transition);

    FooloxExtentionTransitionConfigurer<S,E> transition(S event) ;
}
