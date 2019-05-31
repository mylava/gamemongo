package com.foolox.game.core.statemachine.action;

import com.foolox.game.core.statemachine.impl.FooloxExtentionTransitionConfigurer;
import com.foolox.game.core.statemachine.message.Message;

/**
 * comment:
 *
 * @author: lipengfei
 * @date: 29/05/2019
 */
public interface Action<T, S> {
    void execute(Message<T> message , FooloxExtentionTransitionConfigurer<T, S> configurer);
}
