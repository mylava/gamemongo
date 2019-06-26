package com.foolox.game.core.server;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.lang.reflect.Method;

/**
 * comment:
 *
 * @author: lipengfei
 * @date: 25/06/2019
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CommandHandler {
    private Object handler;
    private Method method;
    private String command;
}
