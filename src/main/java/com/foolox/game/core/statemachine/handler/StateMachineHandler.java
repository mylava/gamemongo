package com.foolox.game.core.statemachine.handler;

import com.foolox.game.core.engin.game.state.PlayerEvent;
import com.foolox.game.core.statemachine.action.Action;
import com.foolox.game.core.statemachine.StateMachine;
import com.foolox.game.core.statemachine.config.Transition;
import com.foolox.game.core.statemachine.message.Message;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * comment:
 *
 * @author: lipengfei
 * @date: 03/06/2019
 */
@Data
@Slf4j
@NoArgsConstructor
@AllArgsConstructor
public class StateMachineHandler {
    private StateMachine stateMachine;

    /**
     * Handle event with entity.
     * 1、获得当前事件的Transition
     * 2、处理Action
     * 3、校验当前状态是否是从 上一个状态 转变来的（不做实现）
     * 4、变更状态到下一步
     *
     * @param event   the event
     * @param message the message
     * @return true if event was accepted
     */
    public boolean handleEventWithState(Message message, PlayerEvent event) {
        Transition transition = stateMachine.getTransitionContext().get(event);
        if (transition != null) {
            Action action = transition.getAction();
            if (action != null) {
                /**
                 * 1、任务的执行应该为异步执行，可以考虑放入 RingBuffer中处理
                 * 2、启用异步线程并增加线程池处理
                 */
                action.execute(message, transition);
                /**
                 * 修改当前状态，并持久化
                 */
            } else {
                //抛出异常
                log.info("Transition's Action is null");
            }
        }
        return stateMachine != null;
    }
}
