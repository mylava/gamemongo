package com.foolox.game.common.util.event;

import lombok.Data;
import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * comment:
 *
 * @author: lipengfei
 * @date: 20/05/2019
 */
@Data
public class UserDataEvent {
    private long id;
    //事件
    private UserEvent event;
    //命令
    private UserDataEventType command;
    //dao
    private MongoRepository repository;
}
