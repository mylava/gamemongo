package com.foolox.game.core.statemachine.config;

/**
 * comment: 状态配置
 *
 * @author: lipengfei
 * @date: 29/05/2019
 */
public interface State<S, E> {
    /**
     * Specify a initial addState {@code S}.
     * 指定初始状态
     *
     * @param initial the initial addState
     * @return configurer for chaining
     */
    State<S, E> initial(S initial);

    /**
     * Specify a addState {@code S}.
     * 指定状态
     *
     * @param state the addState
     * @return configurer for chaining
     */
    State<S, E> addState(S state);



    State<S, E> withStates() throws Exception;
}
