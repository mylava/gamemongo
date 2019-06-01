package com.foolox.game.core.logic.dizhu.action;

import com.foolox.game.core.statemachine.action.Action;
import com.foolox.game.core.statemachine.impl.FooloxExtentionTransition;
import com.foolox.game.core.statemachine.message.Message;

/**
 * comment:
 *
 * @author: lipengfei
 * @date: 30/05/2019
 */
public class AutoAction<T, S> implements Action<T, S> {
    @Override
    public void execute(Message<T> message, FooloxExtentionTransition<T, S> configurer) {

    }
}
