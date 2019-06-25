package com.foolox.game.core.server;

import lombok.Builder;
import lombok.Data;

import java.lang.reflect.Method;

/**
 * comment:
 *
 * @author: lipengfei
 * @date: 25/06/2019
 */
@Data
@Builder
public class EventHandler {
    private Object controller;
    private Method method;
    private String command;
}
