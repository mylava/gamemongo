package com.foolox.game.constants;

/**
 * comment: 收入类型
 *
 * @author: lipengfei
 * @date: 31/05/2019
 */
public enum PVAInComeActionEnum {
    //1、充值，2、兑换、3、赢了，4、赠送，5、抽奖，6、接受赠与
    RECHARGE,
    EXCHANGE,
    WIN,
    WELFARE,
    PRIZE,
    GIFT;

    @Override
    public String toString(){
        return super.toString().toLowerCase() ;
    }
}
