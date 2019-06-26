package com.foolox.game.core.server.handler;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * comment:
 *
 * @author: lipengfei
 * @date: 25/06/2019
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Command {
    String value() default "gamestatus";
}
