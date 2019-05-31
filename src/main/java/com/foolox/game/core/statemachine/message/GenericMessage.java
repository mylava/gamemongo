package com.foolox.game.core.statemachine.message;

import java.util.Map;

/**
 * comment: 消息实现类
 *
 * @author: lipengfei
 * @date: 30/05/2019
 */
public class GenericMessage<T> implements Message<T> {
    private final T payload;

    private final MessageHeaders headers;

    public GenericMessage(T payload, Map<String, Object> headers) {
        this(payload, new MessageHeaders(headers));
    }

    public GenericMessage(T payload, MessageHeaders headers) {
        this.payload = payload;
        this.headers = headers;
    }

    @Override
    public MessageHeaders getMessageHeaders() {
        return headers;
    }
    @Override
    public T getPayload() {
        return payload;
    }
}
