package com.foolox.game.constants;

/**
 * comment:
 *
 * @author: lipengfei
 * @date: 18/05/2019
 */
public enum DictType {
    //游戏配置
    GAME_CONFIG,
    //游戏
    GAME_MODEL,
    //游戏类型
    GAME_TYPE,
    //游戏玩法
    GAME_PLAYWAY;

    @Override
    public String toString() {
        return super.toString().toLowerCase();
    }
}
