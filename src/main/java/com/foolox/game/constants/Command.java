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
    CATCH_RESULT,
    //按提示出最小的牌
    CARD_TIPS,
    //上一手牌
    LAST_HANDS,
    //选择碰杠胡过
    SELECT_ACTION,
    //房间所有人都已准备 -->  可以开始游戏了
    ROOM_READY,
    //玩家已经准备好
    PLAYER_READY,
    //聊天
    MESSAGE,
    //新的庄家
    BANKER,
    //打完所有牌，结算
    ALLCARDS;

    @Override
    public String toString() {
        return super.toString().toLowerCase();
    }
}
