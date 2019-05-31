package com.foolox.game.core.statemachine.impl;

import com.foolox.game.core.statemachine.message.GenericMessage;
import com.foolox.game.core.statemachine.message.Message;
import com.foolox.game.core.statemachine.message.MessageHeaders;

/**
 * comment: 消息Builder
 *
 * @author: lipengfei
 * @date: 30/05/2019
 */
public class MessageBuilder<T> {
    private final T payload;

    private MessageHeaders readOnlyHeaders = new MessageHeaders();


    /**
     * 私有化构造器，只能从静态工厂方法中调用
     */
    private MessageBuilder(T payload) {
        this.payload = payload;
    }

    public T getPayload() {
        return this.payload;
    }

    public MessageBuilder<T> setHeader(String headerName, Object headerValue) {
        this.readOnlyHeaders.getHeaders().put(headerName, headerValue) ;
        return this;
    }

    /**
     * 为 MessageBuilder 实例添加 payload (传递信息)
     *
     * @param payload the payload for the new message
     * @param <T> The type of the payload.
     * @return A MessageBuilder.
     */
    public static <T> MessageBuilder<T> withPayload(T payload) {
        return new MessageBuilder<T>(payload);
    }

    public Message<T> build() {
        return new GenericMessage<T>(this.payload, this.readOnlyHeaders);
    }

}
