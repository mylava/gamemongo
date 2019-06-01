package com.foolox.game.core.statemachine.config;

/**
 * comment:
 *
 * @author: lipengfei
 * @date: 01/06/2019
 */
public interface ExternalTransition<S,E> extends ITransition<ExternalTransition<S,E>,S,E,StateMachineTransitionConfigurer<S,E>> {
    @Override
    ExternalTransition<S, E> target(S target);
}
