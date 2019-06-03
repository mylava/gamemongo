package com.foolox.game.core.statemachine.action;

import com.foolox.game.core.statemachine.config.Transition;
import com.foolox.game.core.statemachine.message.Message;

/**
 * comment:
 *
 * @author: lipengfei
 * @date: 29/05/2019
 */
public interface Action {
    void execute(Message message , Transition transition);
}
