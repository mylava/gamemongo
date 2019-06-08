package com.foolox.game.core.logic;

import com.foolox.game.common.repo.domain.ClientSession;
import com.foolox.game.common.repo.domain.GamePlayway;
import com.foolox.game.common.repo.domain.GameRoom;
import com.foolox.game.common.util.GameUtils;
import com.foolox.game.core.FooloxGameProcessor;
import com.foolox.game.core.engin.game.event.Board;
import com.foolox.game.core.engin.game.event.DiZhuBoard;
import com.foolox.game.core.engin.game.event.GamePlayer;
import org.apache.commons.lang3.StringUtils;

import java.util.*;

/**
 * comment:
 *
 * @author: lipengfei
 * @date: 30/05/2019
 */
public class DizhuGameProcessor implements FooloxGameProcessor {
    @Override
    public Board process(List<ClientSession> clientSessionList, GameRoom gameRoom, GamePlayway playway, String banker, int cardsnum) {
        gameRoom.setCurrentnum(gameRoom.getCurrentnum() + 1);
        Board board = new DiZhuBoard() ;
        board.setCards(null);
        List<Byte> temp = new ArrayList<Byte>() ;
        for(int i= 0 ; i<54 ; i++){
            temp.add((byte)i) ;
        }
        /**
         * 洗牌次数，参数指定，建议洗牌次数 为1次，多次洗牌的随机效果更好，例如：7次
         */
        for(int i = 0; i<playway.getShuffleTimes() + 1; i++){
            Collections.shuffle(temp);
        }
        byte[] cards = new byte[54] ;
        for(int i=0 ; i<temp.size() ; i++){
            cards[i] = temp.get(i) ;
        }
        board.setCards(cards);

        board.setRatio(15); 	//默认倍率 15
        int random = clientSessionList.size() * gameRoom.getCardsnum() ;

        board.setPosition((byte)new Random().nextInt(random));	//按照人数计算在随机界牌 的位置，避免出现在底牌里

        GamePlayer[] gamePlayers = new GamePlayer[clientSessionList.size()];

        int inx = 0 ;
        for(ClientSession playUser : clientSessionList){
            GamePlayer player = new GamePlayer(playUser.getUserId()) ;
            player.setCards(new byte[cardsnum]);
            gamePlayers[inx++] = player ;
        }
        for(int i = 0 ; i<gameRoom.getCardsnum()*gameRoom.getMaxPlayerNum(); i++){
            int pos = i%gamePlayers.length ;
            gamePlayers[pos].getCards()[i/gamePlayers.length] = cards[i] ;
            if(i == board.getPosition()){
                gamePlayers[pos].setRandomcard(true);		//起到地主牌的人
            }
        }
        for(GamePlayer tempPlayer : gamePlayers){
            Arrays.sort(tempPlayer.getCards());
            tempPlayer.setCards(GameUtils.reverseCards(tempPlayer.getCards()));
        }
        board.setRoom(gameRoom.getId());
        GamePlayer tempbanker = gamePlayers[0];
        if(!StringUtils.isBlank(banker)){
            for(int i= 0 ; i<gamePlayers.length ; i++){
                GamePlayer player = gamePlayers[i] ;
                if(player.getPlayuserId().equals(banker)){
                    if(i < (gamePlayers.length - 1)){
                        tempbanker = gamePlayers[i+1] ;
                    }
                }
            }

        }
        board.setGamePlayers(gamePlayers);
        if(tempbanker!=null){
            board.setBanker(tempbanker.getPlayuserId());
        }
        return board;
    }
}
