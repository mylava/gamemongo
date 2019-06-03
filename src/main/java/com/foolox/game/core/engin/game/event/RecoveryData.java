package com.foolox.game.core.engin.game.event;

import com.foolox.game.constants.Command;
import com.foolox.game.core.engin.game.GameBoard;
import com.foolox.game.core.engin.game.Message;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/**
 * comment:
 *
 * @author: lipengfei
 * @date: 29/05/2019
 */
@Data
@NoArgsConstructor
public class RecoveryData implements Message {
    private Command command;
    private String userid;
    private GamePlayer gamePlayer;
    private byte[] lasthands;
    private TakeCards last;
    private Banker banker;
    private String nextplayer;//正在出牌的玩家
    private CardsInfo[] cardsnum; //手牌信息
    private int time;        //计时器剩余时间
    private boolean automic;    //本轮第一个出牌，不允许出现不出按钮
    private GameBoard data;
    private int ratio;
    private byte[] hiscards;

    private SelectColor selectcolor;

    private UserBoard userboard;

    private String event;

    public RecoveryData(GamePlayer gamePlayer, byte[] lasthands, String nextplayer, int time, boolean automic, Board board) {
        this.gamePlayer = gamePlayer;
        this.userid = gamePlayer.getPlayuserId();
        this.lasthands = lasthands;
        this.nextplayer = nextplayer;
        this.banker = new Banker(board.getBanker());
        this.time = time;
        this.automic = automic;
        this.data = new GameBoard(board.getBanker(), board.getRatio());
        this.hiscards = gamePlayer.getHistory();
        this.ratio = board.getRatio();
        this.userboard = new UserBoard(board, gamePlayer.getPlayuserId(), Command.PLAY);
        this.selectcolor = new SelectColor(board.getBanker(), gamePlayer.getPlayuserId());
        this.selectcolor.setColor(gamePlayer.getColor());
        this.last = board.getLast();
        this.cardsnum = new CardsInfo[board.getGamePlayers().length - 1];
        List<CardsInfo> tempList = new ArrayList<CardsInfo>();
        for (GamePlayer temp : board.getGamePlayers()) {
            if (!temp.getPlayuserId().equals(gamePlayer.getPlayuserId())) {
                tempList.add(new CardsInfo(temp.getPlayuserId(), temp.getCards().length, temp.getHistory(), temp.getActions(), board, temp));
            }
        }
        cardsnum = tempList.toArray(this.cardsnum);
    }
}
