package com.foolox.game.common.util.disruptor;

import com.foolox.game.common.util.event.UserDataEvent;
import com.foolox.game.common.util.event.UserDataEventType;
import com.lmax.disruptor.EventHandler;

/**
 * comment:
 *
 * @author: lipengfei
 * @date: 20/05/2019
 */
public class UserEventHandler implements EventHandler<UserDataEvent> {

    @SuppressWarnings("unchecked")
    @Override
    public void onEvent(UserDataEvent userDataEvent, long arg1, boolean arg2)
            throws Exception {
        if(UserDataEventType.SAVE == userDataEvent.getCommand()){
            if(userDataEvent.getRepository()!=null){
                userDataEvent.getRepository().save(userDataEvent.getEvent()) ;
            }
        }else if(UserDataEventType.DELETE == userDataEvent.getCommand()){
            if(userDataEvent.getRepository()!=null){
                userDataEvent.getRepository().delete(userDataEvent.getEvent()) ;
            }
        }
    }
}
