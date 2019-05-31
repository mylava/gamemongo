package com.foolox.game.common.util.client;

/**
 * comment: 提供静态方法以访问 FooloxClientCache
 *
 * @author: lipengfei
 * @date: 19/05/2019
 */
public class FooloxClientContext {
    private static FooloxClientCache fooloxClientCache = new FooloxClientCache();

    public static FooloxClientCache getFooloxClientCache(){
        return fooloxClientCache;
    }

}
