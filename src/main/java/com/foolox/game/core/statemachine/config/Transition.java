package com.foolox.game.core.statemachine.config;

/**
 * comment: 状态转换配置顶层接口
 *
 * @author: lipengfei
 * @date: 30/05/2019
 */
public interface Transition<I> {
    /**
     * 添加状态转换
     * @return
     */
    I and();
}
