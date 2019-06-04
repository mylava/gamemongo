package com.foolox.game.config;

import com.foolox.game.constants.RoomStatus;
import com.foolox.game.core.engin.game.state.PlayerEvent;
import com.foolox.game.core.logic.action.*;
import com.foolox.game.core.logic.action.dizhu.AutoAction;
import com.foolox.game.core.logic.action.dizhu.DizhuPlayCardsAction;
import com.foolox.game.core.logic.action.dizhu.RaiseHandsAction;
import com.foolox.game.core.statemachine.StateMachine;
import com.foolox.game.core.statemachine.config.State;
import com.foolox.game.core.statemachine.config.Transition;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * comment:
 *
 * @author: lipengfei
 * @date: 30/05/2019
 */
@Configuration
public class DizhuStateMachineConfig {
    @Bean("dizhuStateMachine")
    public StateMachine create() throws Exception {
        StateMachine stateMachine = new StateMachine();
        this.stateConfig(stateMachine.getStateContext());
        this.configure(stateMachine);
        return stateMachine;
    }

    public void stateConfig(State state) {
        state
                .firstState(RoomStatus.NONE)
                .nextState(RoomStatus.CRERATED)
                .nextState(RoomStatus.WAITTING)
                .nextState(RoomStatus.READY)
                .nextState(RoomStatus.BEGIN)
                .nextState(RoomStatus.PLAY)
                .nextState(RoomStatus.END);
    }

    public void configure(StateMachine stateMachine)
            throws Exception {
        /**
         * 状态切换：BEGIN->WAITTING->READY->PLAY->END
         */
        stateMachine
                .addTransition(new Transition()
                        .source(RoomStatus.NONE).target(RoomStatus.CRERATED)
                        .event(PlayerEvent.ENTER).action(new EnterAction()))
                .addTransition(new Transition()
                        .source(RoomStatus.CRERATED).target(RoomStatus.WAITTING)
                        .event(PlayerEvent.JOIN).action(new JoinAction()))
                .addTransition(new Transition()
                        .source(RoomStatus.WAITTING).target(RoomStatus.READY)
                        .event(PlayerEvent.ENOUGH).action(new EnoughAction()))
                .addTransition(new Transition()
                        .source(RoomStatus.READY).target(RoomStatus.BEGIN)
                        .event(PlayerEvent.AUTO).action(new AutoAction()))
                .addTransition(new Transition()
                        .source(RoomStatus.BEGIN).target(RoomStatus.LASTHANDS)
                        .event(PlayerEvent.RAISEHANDS).action(new RaiseHandsAction()))
                .addTransition(new Transition()
                        .source(RoomStatus.LASTHANDS).target(RoomStatus.PLAY)
                        .event(PlayerEvent.PLAYCARDS).action(new DizhuPlayCardsAction()))
                .addTransition(new Transition()
                        .source(RoomStatus.PLAY).target(RoomStatus.END)
                        .event(PlayerEvent.ALLCARDS).action(new AllCardsAction()))
        ;
    }
}
