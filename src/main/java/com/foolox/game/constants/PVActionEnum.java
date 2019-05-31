package com.foolox.game.constants;

/**
 * comment:
 *
 * @author: lipengfei
 * @date: 30/05/2019
 */
public enum PVActionEnum {
    INCOME,	//
    CONSUME,
    EXCHANGE,
    VERIFY;

    @Override
    public String toString(){
        return super.toString().toLowerCase() ;
    }
}
