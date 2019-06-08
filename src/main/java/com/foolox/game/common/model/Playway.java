package com.foolox.game.common.model;

import com.foolox.game.common.repo.domain.OtherRules;
import com.foolox.game.constants.RoomType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * comment: 具体玩法
 *
 * @author: lipengfei
 * @date: 16/05/2019
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Playway {
    private String id;
    private String name;
    private String code;
    //    游戏的自定义配置，后台管理界面上的配置功能
    private Integer score;      //底分
    private Integer minScore;   //进入房间最少分
    private Integer maxScore;   //最大房间最大分
    private Integer onlineCount ;	//在线用户数
    private Integer shuffleTimes;    //洗牌次数 0 表示不洗牌
    private String level;       //级别
    private String iconUrl;        //图标url
    private String memo;        //备注
    private String title;    //玩法标题
    private RoomType roomtype;    //房间类型， 房卡：大厅：俱乐部
    private OtherRules otherRules;    //扩展属性配置（房自定义规则）

}
