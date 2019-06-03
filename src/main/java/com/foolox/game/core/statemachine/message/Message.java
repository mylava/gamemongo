package com.foolox.game.core.statemachine.message;

import com.foolox.game.core.engin.game.state.PlayerEvent;

/**
 * comment: 消息接口，用于在状态转换传递消息时作为消息的载体
 *
 * @author: lipengfei
 * @date: 29/05/2019
 */
public interface Message {
    /**
     * Return the message payload.
     */
    PlayerEvent getPayload();

    /**
     * Return message headers for the message (never {@code null} but may be empty).
     */
    MessageHeaders getMessageHeaders();
}
