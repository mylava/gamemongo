package com.foolox.game.core.engin.game.event;

import com.foolox.game.common.repo.domain.ClientSession;
import com.foolox.game.constants.Command;
import com.foolox.game.core.engin.game.Message;
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
public class RoomPlayers implements Message {
    private int maxPlayerNum ;
    private Command command ;
    private List<ClientSession> clientSessionList;
    private String event ;

    public RoomPlayers(int maxPlayerNum , List<ClientSession> clientSessionList , Command command){
        this.maxPlayerNum = maxPlayerNum ;
        this.clientSessionList = clientSessionList ;
        this.command = command ;
    }
}
