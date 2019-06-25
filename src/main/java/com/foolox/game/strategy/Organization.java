package com.foolox.game.strategy;

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
@Target(ElementType.TYPE) //这个注解只能作用于类
@Retention(RetentionPolicy.RUNTIME) //注解保留策略
public @interface Organization {
    String name() default "foolox";
}
