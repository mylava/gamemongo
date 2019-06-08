package com.foolox.game.core.engin.game.event;

import com.foolox.game.common.repo.domain.GameRoom;
import com.foolox.game.constants.Command;
import com.foolox.game.core.engin.game.Message;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * comment: 所有人都已经准备（开始游戏）
 *
 * @author: lipengfei
 * @date: 31/05/2019
 */
@Data
@NoArgsConstructor
public class RoomReady implements Message {
    private Command command;
    private boolean cardroom;
    private String roomid;
    private String event;

    public RoomReady(GameRoom gameRoom) {
        this.cardroom = gameRoom.isCardroom();
        this.roomid = gameRoom.getId();
    }
}
