package com.foolox.game.common.repo.domain;

import com.foolox.game.common.util.event.UserEvent;
import com.foolox.game.constants.PlayerGameStatus;
import com.foolox.game.constants.PlayerType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

/**
 * comment: 客户端会话对象
 *
 * @author: lipengfei
 * @date: 12/05/2019
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "client_session")
public class ClientSession implements UserEvent {
    /**
     * --------------- ---------------
     * 基本信息,与房间、游戏无关
     * --------------- ---------------
     */
    @Id
    private String id;
    private String token;
    private String userId;
    private String username;
    //使用用户密码的MD5摘要，存储在客户端会话中
    private String password;
    //是否登录
    private boolean login;
    //socket是否在线
    private boolean online=false;
    //用户头像,空表示没有上传
    private String headimg;
    //会话创建时间
    private Date createtime = new Date();
    private Date updatetime = new Date();
    //最后登录时间
    private Date lastlogintime = new Date();

    /**
     * --------------- ---------------
     * 资产信息
     * --------------- ---------------
     */
    private long coins;     //金币数量

    /**
     * --------------- ---------------
     * 机构相关信息
     * --------------- ---------------
     */
    //组织机构(租户)
    private String orgi;

    /**
     * --------------- ---------------
     * 游戏相关信息
     * --------------- ---------------
     */
    private String roomId;      //加入的房间ID
    private boolean roomready;  //在房间中已经准备就绪
    private PlayerGameStatus playerGameStatus;    //玩家在游戏中的状态 ： READY : NOTREADY : PLAYING ：MANAGED/托管
    private PlayerType playerType;    //玩家类型 ： 普通玩家/托管玩家/AI/离开房间玩家
    private long playerindex;   //玩家进入房间的顺序

    private boolean opendeal ;	//是否准备(同意开始)


}
