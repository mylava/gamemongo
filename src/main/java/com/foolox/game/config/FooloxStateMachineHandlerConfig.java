package com.foolox.game.config;

import com.foolox.game.common.model.Game;
import com.foolox.game.core.statemachine.FooloxStateMachine;
import com.foolox.game.core.statemachine.impl.FooloxMachineHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.Resource;

/**
 * comment:
 *
 * @author: lipengfei
 * @date: 30/05/2019
 */
@Configuration
public class FooloxStateMachineHandlerConfig {
    @Resource(name="dizhu")
    private FooloxStateMachine<String,String> dizhuConfigure ;

    @Resource(name="majiang")
    private FooloxStateMachine<String,String> maJiangConfigure ;

    @Bean("dizhuGame")
    public Game dizhu() {
        return new Game(new FooloxMachineHandler(this.dizhuConfigure));
    }

    @Bean("majiangGame")
    public Game majiang() {
        return new Game(new FooloxMachineHandler(this.maJiangConfigure));
    }
}
