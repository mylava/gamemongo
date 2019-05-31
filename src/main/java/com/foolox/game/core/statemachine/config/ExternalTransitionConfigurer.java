package com.foolox.game.core.statemachine.config;

/**
 * comment: 外部状态转换配置接口
 *
 * @author: lipengfei
 * @date: 29/05/2019
 */
public interface ExternalTransitionConfigurer<S, E> extends TransitionConfigurer<ExternalTransitionConfigurer<S, E>, S, E> {
    public ExternalTransitionConfigurer<S, E> target(S target);
}
