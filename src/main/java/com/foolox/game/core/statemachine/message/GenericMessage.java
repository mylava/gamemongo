package com.foolox.game.core.statemachine.message;

import com.foolox.game.core.engin.game.state.PlayerEvent;

import java.util.Map;

/**
 * comment: 消息实现类
 *
 * @author: lipengfei
 * @date: 30/05/2019
 */
public class GenericMessage implements Message {
    private final PlayerEvent payload;

    private final MessageHeaders headers;

    public GenericMessage(PlayerEvent payload, Map<String, Object> headers) {
        this(payload, new MessageHeaders(headers));
    }

    public GenericMessage(PlayerEvent payload, MessageHeaders headers) {
        this.payload = payload;
        this.headers = headers;
    }

    @Override
    public MessageHeaders getMessageHeaders() {
        return headers;
    }
    @Override
    public PlayerEvent getPayload() {
        return payload;
    }
}
