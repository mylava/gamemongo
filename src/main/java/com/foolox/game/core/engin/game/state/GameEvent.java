package com.foolox.game.core.engin.game.state;

import com.foolox.game.common.repo.domain.GameRoom;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * comment: 游戏状态 （这里使用状态模式）
 *
 * @author: lipengfei
 * @date: 20/05/2019
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class GameEvent {
    //房间ID
    public String roomid;
    //事件
    private PlayerEvent eventType;
    //游戏房间对象
    private GameRoom gameRoom;
    //当前玩家 顺序号
    private int index;
    //游戏人数
    private int players;
    //每个玩家发牌数量
    private int cardsnum;
    private long time;


    public GameEvent(int players) {
        this.players = players;
    }

    public GameEvent(int players, int cardsnum) {
        this.players = players;
        this.cardsnum = cardsnum;
    }
}
