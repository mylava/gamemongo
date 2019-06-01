package com.foolox.game.core.statemachine.impl;

import com.foolox.game.core.statemachine.action.Action;
import com.foolox.game.core.statemachine.config.ExternalTransition;
import com.foolox.game.core.statemachine.config.StateMachineTransitionConfigurer;
import lombok.Data;

/**
 * comment: 瓜牛外部转换配置
 *
 * @author: lipengfei
 * @date: 29/05/2019
 */
@Data
public class FooloxExtentionTransition<T, S> implements ExternalTransition<T, S> {
    private StateMachineTransitionConfigurer<T, S> configure ;
    private T source = null , target = null ;
    private S event = null;
    private Action<T, S> action = null , errorAction = null;

    public FooloxExtentionTransition(StateMachineTransitionConfigurer<T, S> configure){
        this.configure = configure ;
    }

    @Override
    public ExternalTransition<T, S> source(T source) {
        this.source = source ;
        return this;
    }

    @Override
    public ExternalTransition<T, S> event(S event) {
        this.event = event ;
        return this;
    }

    @Override
    public ExternalTransition<T, S> action(Action<T, S> action) {
        this.action = action;
        return this;
    }

    @Override
    public ExternalTransition<T, S> action(Action<T, S> action,
                                           Action<T, S> error) {
        this.action = action;
        this.errorAction = error ;
        return null;
    }
    @Override
    public StateMachineTransitionConfigurer<T, S> and() {
        configure.apply(this);
        return configure;
    }

    @Override
    public ExternalTransition<T, S> target(T target) {
        this.target = target ;
        return this;
    }
}
