package com.foolox.game.constants;

/**
 * comment:
 *
 * @author: lipengfei
 * @date: 31/05/2019
 */
public enum SearchRoomResultType {
    NOTEXIST,  //房间不存在
    FULL, 		//房间已满员
    OK,			//加入成功
    DISABLE,	//房间启用了 禁止非邀请加入
    INVALID;	//房主已离开房间

    @Override
    public String toString(){
        return super.toString().toLowerCase() ;
    }
}
