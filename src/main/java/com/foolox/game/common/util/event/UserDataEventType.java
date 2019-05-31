package com.foolox.game.common.util.event;

/**
 * comment:
 *
 * @author: lipengfei
 * @date: 20/05/2019
 */
public enum UserDataEventType {
    SAVE,UPDATE,DELETE;
    @Override
    public String toString(){
        return super.toString().toLowerCase() ;
    }
}
