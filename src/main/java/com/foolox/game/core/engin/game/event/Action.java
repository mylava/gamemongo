package com.foolox.game.core.engin.game.event;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * comment: 打牌动作
 *
 * @author: lipengfei
 * @date: 29/05/2019
 */
@Data
@NoArgsConstructor
public class Action {
    private byte card;
    private String action;
    private String type;            //动作类型， 杠 ： 明杠|暗杠|弯杠  ，  胡：胡法
    private String userid;
    private boolean gang;            //碰了以后，是否已再杠

    public Action(String userid , String action , String type , byte card){
        this.userid = userid ;
        this.action = action ;
        this.type = type ;
        this.card = card ;
    }

    public Action(String userid , String action ,byte card){
        this.userid = userid ;
        this.action = action ;
        this.card = card ;
    }
}
