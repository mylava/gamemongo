package com.foolox.game.core.logic;

import com.foolox.game.core.statemachine.action.Action;
import com.foolox.game.core.statemachine.impl.FooloxExtentionTransitionConfigurer;
import com.foolox.game.core.statemachine.message.Message;

/**
 * comment:
 *
 * @author: lipengfei
 * @date: 30/05/2019
 */
public class JoinAction<T, S> implements Action<T, S> {
    @Override
    public void execute(Message<T> message, FooloxExtentionTransitionConfigurer<T, S> configurer) {

    }
}
