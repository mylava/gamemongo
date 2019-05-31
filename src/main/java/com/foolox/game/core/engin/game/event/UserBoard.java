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
public class UserBoard implements Message {
    private GamePlayer player ;
    private GamePlayer[] players ;
    private int deskcards ;	//剩下多少张牌
    private Command command ;
    private String event ;

    /**
     * 发给玩家的牌，开启特权后可以将某个其他玩家的牌 显示出来
     * @param board
     * @param curruser
     */
    public UserBoard(Board board , String curruser , Command command){
        players = new GamePlayer[board.getGamePlayers().length-1] ;
        this.command = command ;
        if(board.getDeskcards()!=null){
            this.deskcards = board.getDeskcards().size() ;
        }
        int inx = 0 ;
        for(GamePlayer temp : board.getGamePlayers()){
            if(temp.getPlayuserId().equals(curruser)){
                player = temp ;
            }else{
                GamePlayer clonePlayer = temp.clone() ;
                clonePlayer.setDeskcards(clonePlayer.getCards().length);
                clonePlayer.setCards(null);	//克隆对象，然后将 其他玩家手里的牌清空
                players[inx++] = clonePlayer;
            }
        }
    }
}
