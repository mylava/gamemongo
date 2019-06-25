package com.foolox.game.core.engin.game.state;

/**
 * comment: 所有棋牌类游戏 的基本状态
 * 根据游戏类型不同，状态下的事件有所不同
 *
 * @author: lipengfei
 * @date: 26/05/2019
 */
public enum PlayerEvent {
    //创建房间 （仅第一个加入房间的人触发的事件）
    ENTER,
    JOIN,		//成员加入
    AUTO,		//自动 , 抢地主
    ENOUGH,		//凑够一桌子
    RAISEHANDS,	//流程处理完毕，开始出牌
    PLAYCARDS,	//出牌
    ALLCARDS,	//1、单个玩家打完牌（地主，推到胡）；2、打完桌面的所有牌（血战，血流，德州）
    DEAL,		//抓牌动作
    SELECT ;	//麻将的特别事件 ， 定缺
}
