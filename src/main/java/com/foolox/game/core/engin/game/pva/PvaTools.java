package com.foolox.game.core.engin.game.pva;

/**
 * comment:
 *
 * @author: lipengfei
 * @date: 30/05/2019
 */
public class PvaTools {
    private static Pva goldPvaImpl = new GoldPVAImpl();
    /**
     *
     * @return
     */
    public static Pva getGoldCoins(){
        return goldPvaImpl;
    }
}
