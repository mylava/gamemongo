package com.foolox.game.core.engin.game.event;

import com.foolox.game.constants.Command;
import com.foolox.game.core.engin.game.Message;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * comment: 庄家
 *
 * @author: lipengfei
 * @date: 29/05/2019
 */
@Data
@NoArgsConstructor
public class Banker implements Message {
    private Command command;
    private String userid;
    private String event;

    public Banker(String userid) {
        this.userid = userid;
    }
}
