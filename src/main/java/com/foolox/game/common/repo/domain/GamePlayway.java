package com.foolox.game.common.repo.domain;

import com.foolox.game.constants.RoomType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

/**
 * comment: 具体某个游戏的玩法 相当于 playtypedto
 *
 * @author: lipengfei
 * @date: 11/05/2019
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "game_playway")
public class GamePlayway {
    @Id
    private String id;
    private String name;
    //GameModel.code
    private String modelCode; //游戏类型 ： 麻将：地主：德州
    //创建时间
    private Date createtime;
    //GameType.code
    private String typeCode;
    private String parentId;
    private String creater;
    private Integer sortIndex = 1;    //排序编号
    private String username;

    private String level;    //初|中|高级等  1表示最低
    private String iconUrl;    //玩法图标
    private Integer score;        //底分
    private Integer minScore;    //最小分数
    private Integer maxScore;    //最大分数
    private Integer shuffleTimes=1;    //洗牌次数 0 表示不洗牌
    private int cardsNum;    //每个玩家获牌数量

    private Date updatetime;
    private String area;
    private Integer maxPlayerNum=4;    //最大游戏人数
    private Integer minPlayerNum=2;    //最小游戏人数（达到最小人数就可以开始游戏）
    private Integer numOfGames;//局数 ， 大厅游戏为 0 表示 无限
    private RoomType roomtype;    //房间类型， 房卡：大厅：俱乐部
    private String memo;        //备注信息，不超过30个字
    private String title;    //玩法标题

    private String wintype;//胡牌方式，推倒胡，血战 、 血流
    private OtherRules otherRules;    //扩展属性配置（房自定义规则）
}
