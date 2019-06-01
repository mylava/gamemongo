package com.foolox.game.core.statemachine.impl;

import com.foolox.game.core.statemachine.config.State;

import java.util.LinkedList;

/**
 * comment: 瓜牛状态配置
 *
 * @author: lipengfei
 * @date: 29/05/2019
 */
public class FooloxState<T, S> implements State<T, S> {
    private LinkedList<T> stateContextList = new LinkedList<T>();
    private T initial;    //初始状态

    @Override
    public State<T, S> initial(T initial) {
        this.initial = initial;
        this.stateContextList.add(initial);    //首个元素
        return this;
    }

    @Override
    public State<T, S> addState(T state) {
        this.stateContextList.add(state);
        return this;
    }

    @Override
    public State<T, S> withStates() throws Exception {
        return this;
    }

    public LinkedList<T> getStateContextList() {
        return stateContextList;
    }

    public void setStateContextList(LinkedList<T> stateContextList) {
        this.stateContextList = stateContextList;
    }

    public T getInitial() {
        return initial;
    }

    public void setInitial(T initial) {
        this.initial = initial;
    }
}