package com.foolox.game.core.engin.game.event;

import com.foolox.game.common.repo.domain.ClientSession;
import com.foolox.game.common.repo.domain.GameRoom;
import com.foolox.game.constants.Command;
import com.foolox.game.core.engin.game.Message;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * comment: 加入房间事件
 *
 * @author: lipengfei
 * @date: 29/05/2019
 */
@Data
@NoArgsConstructor
public class JoinRoom implements Message {
    //命令
    private Command command;
    //用户
    private ClientSession clientSession;
    //作为Index
    private int index;
    //最大玩家数
    private int maxplayers;
    //是否房卡房
    private boolean cardroom;
    //房间Id
    private String roomid;
    //事件
    private String event;

    public JoinRoom(ClientSession clientSession, int index, int maxplayer, GameRoom gameRoom) {
        this.clientSession = clientSession;
        this.index = index;
        this.maxplayers = maxplayer;
        this.cardroom = gameRoom.isCardroom();
        this.roomid = gameRoom.getId();
    }
}
