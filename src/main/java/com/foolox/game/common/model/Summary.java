package com.foolox.game.common.model;

import com.foolox.game.constants.Command;
import com.foolox.game.core.engin.game.Message;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/**
 * comment: 结算信息
 *
 * @author: lipengfei
 * @date: 29/05/2019
 */
@Data
@NoArgsConstructor
public class Summary implements Message {
    private String game;        //房间ID
    private String board;        //场次 ID
    private int ratio;            //倍率
    private Command command;
    private boolean finished = true;
    private boolean gameRoomOver;
    private String event;
    private int score ;			//总分
    private List<SummaryPlayer> players = new ArrayList<SummaryPlayer>() ;

    public Summary(String game, String board, int ratio, int score) {
        this.game = game;
        this.board = board;
        this.ratio = ratio;
        this.score = score;
    }
}
