package com.foolox.game.common.repo.domain;

import com.foolox.game.common.util.event.UserEvent;
import com.foolox.game.constants.RoomStatus;
import com.foolox.game.constants.RoomType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;
import java.util.Map;

/**
 * comment:
 *
 * @author: lipengfei
 * @date: 20/05/2019
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "gameroom")
public class GameRoom implements UserEvent {
    @Id
    private String id; //房间ID，房卡游戏的 房间ID是 6位数字，其他为 UUID
    private String name;
    private String code;

    private boolean matchmodel;    //是否比赛房间
    private String matchid;//赛事ID
    private int matchscreen;//比赛场次
    private String matchtype;    //比赛类型

    private String lastwinner;    //最后赢的人Id ， 可多人 ， 逗号隔开


    private Date createtime;
    private String parentid;
    private String typeid;
    private String creater;
    private String username;

    private RoomStatus status;    //当前状态

    private Date updatetime;
    private String area;

    private String game;    //游戏类型 ： 麻将：地主：德州
    private int maxPlayerNum;    //最大游戏人数
    private int cardsnum;    //发牌数量
    private int curpalyers;    //当前人数
    //STAY 先预留
    private boolean cardroom;    //是否房卡模式

    private String master;    //房主 ，开设房间的人 或第一个进入的人,对应 PlayerId

    private RoomType roomtype;    //房间类型， 房卡：大厅：俱乐部

    private String playwayId;    //玩法

    private int numofgames;    //局数
    private int currentnum;    //已完局数

    private Player masterUser;    //房间的创建人
    private GamePlayway gamePlayway;    //房间玩法

    private Map<String, String> extparams;//房卡模式下的自定义参数
}
