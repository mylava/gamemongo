package com.foolox.game.core.engin.game.event;

import com.foolox.game.constants.Command;
import com.foolox.game.core.engin.game.Message;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * comment:
 *
 * @author: lipengfei
 * @date: 29/05/2019
 */
@Data
@NoArgsConstructor
public class SelectColor implements Message {
    private String banker;
    private String userId;
    private int color = 10;
    private long time;
    private String event;
    private Command command;

    public SelectColor(String banker){
        this.banker = banker ;
    }
    public SelectColor(String banker , String userId){
        this.userId = userId;
        this.banker = banker ;
    }

}
