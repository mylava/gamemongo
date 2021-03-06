package com.foolox.game.common.model;

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
public class SummaryPlayer {
    private String userid ;
    private String username ;
    private int ratio ;
    private long score ;
    private boolean gameover ;//破产了
    private long balance ;	  //玩家账户余额
    private boolean win ;
    private byte[] cards ;

    private boolean dizhu ;

    public SummaryPlayer(String userid , String username , int ratio , int score, boolean win , boolean dizhu){
        this.userid = userid ;
        this.username = username ;
        this.ratio = ratio ;
        this.score = score ;
        this.win = win ;
        this.dizhu = dizhu ;
    }
}
