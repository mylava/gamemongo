package com.foolox.game.constants;

/**
 * comment: 支出类型
 *
 * @author: lipengfei
 * @date: 31/05/2019
 */
public enum  PVAConsumeActionEnum {
    // 1、输了，2、逃跑扣除、3、兑换扣除，4、送好友
    LOST,
    ESCAPE,
    DEDUCTION,
    SEND,
    SUBSIDY;

    @Override
    public String toString(){
        return super.toString().toLowerCase() ;
    }
}
