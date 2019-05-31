package com.foolox.game.constants;

/**
 * comment: 玩家的游戏状态
 *
 * @author: lipengfei
 * @date: 19/05/2019
 */
public enum PlayerGameStatus {
    //准备
    READY,
    NOTREADY,		//未准备
    MANAGED,        //托管
    PLAYING,        //游戏中(包含结算中？)
    SETTLEMENT,     //结算中
    TIMEOUT;		//会话超时
    @Override
    public String toString(){
        return super.toString().toLowerCase() ;
    }
}
