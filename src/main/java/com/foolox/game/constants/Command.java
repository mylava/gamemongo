package com.foolox.game.constants;

/**
 * comment:
 *
 * @author: lipengfei
 * @date: 29/05/2019
 */
public enum Command {
    //加入房间
    JOIN_ROOM,
    //查询房间内玩家
    GET_ROOM_PLAYERS,
    //恢复
    RECOVERY,
    //定缺
    SELECT_RESULT,
    //游戏中
    PLAY,
    //翻倍
    RATIO,
    //出牌
    TAKECARDS,
    //抢地主
    CATCH,
    //抢地主结果
    CATCH_RESULT;

    @Override
    public String toString() {
        return super.toString().toLowerCase();
    }
}
