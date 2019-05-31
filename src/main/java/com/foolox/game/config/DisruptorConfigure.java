package com.foolox.game.config;

import com.foolox.game.common.util.disruptor.DisruptorExceptionHandler;
import com.foolox.game.common.util.disruptor.UserDataEventFactory;
import com.foolox.game.common.util.disruptor.UserEventHandler;
import com.foolox.game.common.util.event.UserDataEvent;
import com.lmax.disruptor.SleepingWaitStrategy;
import com.lmax.disruptor.dsl.Disruptor;
import com.lmax.disruptor.dsl.ProducerType;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * comment: 并发框架disruptor
 *
 * @author: lipengfei
 * @date: 20/05/2019
 */
@Component
public class DisruptorConfigure {

    @Bean(name="disruptor")
    public Disruptor<UserDataEvent> disruptor() {
        //创建线程池
        ThreadFactory threadFactory = new ThreadFactory() {
            private final AtomicInteger index = new AtomicInteger(1);
            @Override
            public Thread newThread(Runnable runnable) {
                return new Thread(null, runnable, "disruptor-thread-" + index.getAndIncrement());
            }
        };
        //创建事件工厂
        UserDataEventFactory factory = new UserDataEventFactory();
        //创建Disruptor
        Disruptor<UserDataEvent> disruptor = new Disruptor<UserDataEvent>(factory,1024,threadFactory,ProducerType.SINGLE,new SleepingWaitStrategy());
        //添加异常处理器
        disruptor.setDefaultExceptionHandler(new DisruptorExceptionHandler());
        //添加事件处理器
        disruptor.handleEventsWith(new UserEventHandler());
        //启动
        disruptor.start();
        return disruptor;
    }
}
