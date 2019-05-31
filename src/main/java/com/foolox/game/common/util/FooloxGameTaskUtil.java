package com.foolox.game.common.util;

import com.foolox.game.core.engin.game.FooloxGameTask;
import org.cache2k.Cache;
import org.cache2k.Cache2kBuilder;
import org.cache2k.CacheEntry;
import org.cache2k.event.CacheEntryExpiredListener;
import org.cache2k.expiry.ExpiryPolicy;
import org.cache2k.expiry.ValueWithExpiryTime;

/**
 * comment: 定时任务工具类，利用缓存过期触发定时任务
 *
 * @author: lipengfei
 * @date: 14/05/2019
 */
public class FooloxGameTaskUtil {
    //单例
    private static FooloxGameTaskUtil fooloxGameTaskUtil = new FooloxGameTaskUtil();
    //调度缓存
    private final Cache<String, ValueWithExpiryTime> expireCache;

    /**
     * 在构造方法中定义 注册定时任务 的钩子
     */
    private FooloxGameTaskUtil() {
        expireCache = new Cache2kBuilder<String, ValueWithExpiryTime>() {}
                .sharpExpiry(true)
                .eternal(false)
                //过期策略
                .expiryPolicy(new ExpiryPolicy<String, ValueWithExpiryTime>() {
                    @Override
                    public long calculateExpiryTime(String key, ValueWithExpiryTime value,
                                                    long loadTime, CacheEntry<String, ValueWithExpiryTime> oldEntry) {
                        return value.getCacheExpiryTime();
                    }
                })
                //过期监听
                .addListener(new CacheEntryExpiredListener<String, ValueWithExpiryTime>() {
                    @Override
                    public void onEntryExpired(Cache<String, ValueWithExpiryTime> cache,
                                               CacheEntry<String, ValueWithExpiryTime> task) {
                        /**
                         *
                         */
                        ((FooloxGameTask) task.getValue()).execute();
                    }
                })
                .build();
    }

//    public static FooloxGameTaskUtil getInstance(){
//        return fooloxGameTaskUtil ;
//    }
    //注册具体定时任务时调用
    public static Cache<String, ValueWithExpiryTime> getExpireCache() {
        return fooloxGameTaskUtil.expireCache;
    }
}
