package com.foolox.game.core.engin.game;

import com.foolox.game.constants.Command;

/**
 * comment:
 *
 * @author: lipengfei
 * @date: 07/05/2019
 */
public interface Message {

    /**
     * 发送到客户端的指令
     *
     * @return
     */
    Command getCommand();

    /**
     * 指令
     *
     * @param command
     */
    void setCommand(Command command);

    /**
     * 发送到客户端的事件
     *
     * @return
     */
    String getEvent();

    /**
     * 事件
     *
     * @param event
     */
    void setEvent(String event);
}
