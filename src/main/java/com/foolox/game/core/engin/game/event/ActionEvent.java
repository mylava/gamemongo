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
public class ActionEvent implements Message {
    private String banker;
    private String userid;
    private String target;    //目标， 杠/碰的 目标

    private String actype;        //杠 的类型 ， 明杠/暗杠/弯杠
    private String action;
    private byte card;

    private byte cardtype;
    private byte cardvalue;
    private String event;

    private long time;
    private Command command;

    public ActionEvent(String banker){
        this.banker = banker ;
    }
    public ActionEvent(String banker , String userid, byte card , String action){
        this.userid = userid ;
        this.card = card ;
        this.action = action ;
        this.banker = banker ;
        this.cardtype = (byte) (card / 36) ;
        this.cardvalue = (byte) (( card % 36 ) /4) ;
    }
}
