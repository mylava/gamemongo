package com.foolox.game.common.util.disruptor;

import com.foolox.game.common.util.event.UserDataEvent;
import com.lmax.disruptor.EventFactory;

/**
 * comment:
 *
 * @author: lipengfei
 * @date: 20/05/2019
 */
public class UserDataEventFactory implements EventFactory<UserDataEvent> {
    @Override
    public UserDataEvent newInstance() {
        return new UserDataEvent();
    }
}
