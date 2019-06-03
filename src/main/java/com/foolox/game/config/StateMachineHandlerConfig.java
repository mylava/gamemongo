package com.foolox.game.config;

import com.foolox.game.common.model.Game;
import com.foolox.game.core.statemachine.StateMachine;
import com.foolox.game.core.statemachine.handler.StateMachineHandler;
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
public class StateMachineHandlerConfig {
    @Resource(name="dizhuStateMachine")
    private StateMachine dizhuConfigure ;

    @Resource(name="majiangStateMachine")
    private StateMachine maJiangConfigure ;

    @Bean("dizhuGame")
    public Game dizhu() {
        return new Game(new StateMachineHandler(this.dizhuConfigure));
    }

    @Bean("majiangGame")
    public Game majiang() {
        return new Game(new StateMachineHandler(this.maJiangConfigure));
    }
}
