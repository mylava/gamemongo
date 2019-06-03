package com.foolox.game.core.statemachine.config;

import com.foolox.game.constants.RoomStatus;

import java.util.LinkedList;

/**
 * comment:
 *
 * @author: lipengfei
 * @date: 03/06/2019
 */
public class State {
    private LinkedList<RoomStatus> stateContextList = new LinkedList();
    private RoomStatus initial;    //初始状态

    public State firstState(RoomStatus initial) {
        this.initial = initial ;
        this.stateContextList.add(initial) ;	//首个元素
        return this;
    }

    public State nextState(RoomStatus state) {
        this.stateContextList.add(state) ;
        return this;
    }
}
