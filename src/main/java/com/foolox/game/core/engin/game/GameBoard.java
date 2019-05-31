package com.foolox.game.core.engin.game;

import com.foolox.game.constants.Command;
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
public class GameBoard implements Message {
    private byte[] lasthands;
    private String userid;
    private boolean docatch;
    //是否已经被占用
    private boolean grab;
    //概率
    private int ratio;
    //庄家ID
    private String banker;

    private Command command;

    private String event;

    public GameBoard(String userid, boolean docatch, boolean grab, int ratio) {
        this.userid = userid;
        this.docatch = docatch;
        this.ratio = ratio;
        this.grab = grab;
    }

    public GameBoard(String userid, byte[] lasthands, int ratio) {
        this.userid = userid;
        this.lasthands = lasthands;
        this.ratio = ratio;
    }

    public GameBoard(String userid, int ratio) {
        this.userid = userid;
        this.ratio = ratio;
    }

    public GameBoard(String userid, String banker, int ratio) {
        this.userid = userid;
        this.ratio = ratio;
        this.banker = banker;
    }
}
