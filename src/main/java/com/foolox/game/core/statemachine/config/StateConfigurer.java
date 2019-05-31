package com.foolox.game.core.statemachine.config;

/**
 * comment: 状态配置
 *
 * @author: lipengfei
 * @date: 29/05/2019
 */
public interface StateConfigurer<S, E> {
    /**
     * Specify a initial state {@code S}.
     * 指定初始状态
     *
     * @param initial the initial state
     * @return configurer for chaining
     */
    StateConfigurer<S, E> initial(S initial);

    /**
     * Specify a state {@code S}.
     * 指定状态
     *
     * @param state the state
     * @return configurer for chaining
     */
    StateConfigurer<S, E> state(S state);



    StateConfigurer<S, E> withStates() throws Exception;
}
