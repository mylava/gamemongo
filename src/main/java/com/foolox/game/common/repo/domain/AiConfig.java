package com.foolox.game.common.repo.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

/**
 * comment:
 *
 * @author: lipengfei
 * @date: 02/06/2019
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "ai_config")
public class AiConfig {
    @Id
    private String id;
    private Date createtime = new Date();
    private String creater;
    private String username;
    private String name;
    private String playwayId;

    private Boolean enableai;//启用AI
    private Integer waittime = 5; //玩家等待时长

    private Long initcoins;            //初始 金币数量
    private Integer initcards;            //初始房卡数量
    private Integer initdiamonds;        //初始钻石数量

    private String exitcon;        //机器人退出条件
    private Integer maxai;                //最大AI数量

    private Boolean dicinfo;        //从字典获取 AI的用户昵称、头像信息
    private Boolean aichat;        //启用 AI自动聊天功能

}
