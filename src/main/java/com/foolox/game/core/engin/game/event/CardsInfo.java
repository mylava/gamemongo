package com.foolox.game.core.engin.game.event;

import com.foolox.game.constants.Command;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * comment:
 *
 * @author: lipengfei
 * @date: 29/05/2019
 */
@Data
@NoArgsConstructor
public class CardsInfo {
    private String userid;
    private int cardsnum ;
    private byte[] hiscards ;
    private List<Action> actions ;

    private SelectColor selectcolor ;

    public CardsInfo(String userid, int cardsnum){
        this.userid = userid ;
        this.cardsnum = cardsnum ;
    }

    public CardsInfo(String userid, int cardsnum,byte[] hiscards , List<Action> actions , Board board , GamePlayer gamePlayer){
        this.userid = userid ;
        this.cardsnum = cardsnum ;
        this.hiscards = hiscards ;
        this.actions = actions ;
        this.selectcolor = new SelectColor(board.getBanker(),userid) ;
        this.selectcolor.setColor(gamePlayer.getColor());
        this.selectcolor.setCommand(Command.SELECT_RESULT);
    }
}
