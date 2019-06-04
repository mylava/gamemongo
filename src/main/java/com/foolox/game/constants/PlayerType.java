package com.foolox.game.constants;

/**
 * comment: 玩家状态
 *
 * @author: lipengfei
 * @date: 19/05/2019
 */
public enum PlayerType {
    //AI
    AI,
    NORMAL,		//普通玩家
    OFFLINE,	//托管玩家
    LEAVE;		//离开房间的玩家
    @Override
    public String toString(){
        return super.toString().toLowerCase() ;
    }
}
