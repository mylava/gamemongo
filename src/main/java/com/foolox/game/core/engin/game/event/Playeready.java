package com.foolox.game.core.engin.game.event;

import com.foolox.game.constants.Command;
import com.foolox.game.core.engin.game.Message;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * comment:
 *
 * @author: lipengfei
 * @date: 31/05/2019
 */
@Data
@NoArgsConstructor
public class Playeready implements Message {
    private Command command;
    private String userid;
    private String event;
    public Playeready(String userid , Command command){
        this.userid = userid ;
        this.command = command ;
    }
}
