package com.foolox.game.config;

import com.foolox.game.constants.RoomStatus;
import com.foolox.game.core.engin.game.state.GameEventType;
import com.foolox.game.core.logic.AllCardsAction;
import com.foolox.game.core.logic.EnoughAction;
import com.foolox.game.core.logic.EnterAction;
import com.foolox.game.core.logic.JoinAction;
import com.foolox.game.core.logic.majiang.action.DealMJCardAction;
import com.foolox.game.core.logic.majiang.action.MJRaiseHandsAction;
import com.foolox.game.core.logic.majiang.action.PlayMJCardsAction;
import com.foolox.game.core.logic.majiang.action.SelectAction;
import com.foolox.game.core.statemachine.FooloxStateMachine;
import com.foolox.game.core.statemachine.config.State;
import com.foolox.game.core.statemachine.config.StateMachineTransitionConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * comment:
 *
 * @author: lipengfei
 * @date: 30/05/2019
 */
@Configuration
public class MaJiangStateMachineConfig<T, S> {
    @Bean("majiang")
    public FooloxStateMachine<String,String> create() throws Exception{
        FooloxStateMachine<String,String> fooloxStateMachine = new FooloxStateMachine<String,String>();
        this.configure(fooloxStateMachine.getConfig());
        this.configure(fooloxStateMachine.getTransitions());
        return fooloxStateMachine;
    }

    public void configure(State<String,String> states)
            throws Exception {
        states
                .withStates()
                .initial(RoomStatus.NONE.toString())
                .addState(RoomStatus.CRERATED.toString())
                .addState(RoomStatus.WAITTING.toString())
                .addState(RoomStatus.READY.toString())
                .addState(RoomStatus.BEGIN.toString())
                .addState(RoomStatus.PLAY.toString())
                .addState(RoomStatus.END.toString());
    }

    public void configure(StateMachineTransitionConfigurer<String, String> transitions)
            throws Exception {
        /**
         * 状态切换：BEGIN->WAITTING->READY->PLAY->END
         */
        transitions
                .withExternal()
                .source(RoomStatus.NONE.toString()).target(RoomStatus.CRERATED.toString())
                .event(GameEventType.ENTER.toString()).action(new EnterAction<String,String>())
                .and()
                .withExternal()
                .source(RoomStatus.CRERATED.toString()).target(RoomStatus.WAITTING.toString())
                .event(GameEventType.JOIN.toString()).action(new JoinAction<String,String>())
                .and()
                .withExternal()
                .source(RoomStatus.WAITTING.toString()).target(RoomStatus.READY.toString())
                .event(GameEventType.ENOUGH.toString()).action(new EnoughAction<String, String>())
                .and()
                .withExternal()
                .source(RoomStatus.READY.toString()).target(RoomStatus.BEGIN.toString())
                .event(GameEventType.AUTO.toString()).action(new SelectAction<String,String>())	//抢地主
                .and()
                .withExternal()
                .source(RoomStatus.BEGIN.toString()).target(RoomStatus.LASTHANDS.toString())
                .event(GameEventType.RAISEHANDS.toString()).action(new MJRaiseHandsAction<String,String>())
                .and()
                .withExternal()
                .source(RoomStatus.LASTHANDS.toString()).target(RoomStatus.LASTHANDS.toString())
                .event(GameEventType.DEAL.toString()).action(new DealMJCardAction<String,String>())
                .and()
                .withExternal()
                .source(RoomStatus.LASTHANDS.toString()).target(RoomStatus.PLAY.toString())
                .event(GameEventType.PLAYCARDS.toString()).action(new PlayMJCardsAction<String,String>())
                .and()
                .withExternal()
                .source(RoomStatus.PLAY.toString()).target(RoomStatus.END.toString())
                .event(GameEventType.ALLCARDS.toString()).action(new AllCardsAction<String,String>())
                .and()
        ;
    }
}
