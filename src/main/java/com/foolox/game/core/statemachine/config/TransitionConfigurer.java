package com.foolox.game.core.statemachine.config;

import com.foolox.game.core.statemachine.action.Action;

/**
 * comment: 状态转换配置接口
 *
 * @author: lipengfei
 * @date: 29/05/2019
 */
//
public interface TransitionConfigurer <T, S, E> extends AbstractTransitionConfigurer<StateMachineTransitionConfigurer<S, E>> {
    /**
     * 为当前Transition指定一个源状态
     *
     * @param source the source state {@code S}
     * @return configurer for chaining
     */
    T source(S source);

    /**
     *
     * @param target
     * @return
     */
    T target(S target);

    /**
     * 为当前Transition指定事件，这个事件将被事件触发器触发
     *
     * @param event the event for transition
     * @return configurer for chaining
     */
    T event(E event);

    /**
     * 为当前Transition指定一个动作
     *
     * @param action the action
     * @return configurer for chaining
     */
    T action(Action<S, E> action);

    /**
     * 为当前Transition指定一个动作
     *
     * @param action the action
     * @param error action that will be called if any unexpected exception is thrown by the action.
     * @return configurer for chaining
     */
    T action(Action<S, E> action, Action<S, E> error);
}
