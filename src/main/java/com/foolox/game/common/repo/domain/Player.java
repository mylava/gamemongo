package com.foolox.game.common.repo.domain;

import com.foolox.game.common.util.event.UserEvent;
import com.foolox.game.constants.PlayerType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "player")
public class Player implements UserEvent {
    @Id
    private String id;

    private String username;

    private String password;

    private String email;

    private String gender;

    private String nickname;

    private String userType;

    private String mobile;

    private String headimg;
    //0冻结 1正常
    private Integer state;

    private Long balance;

    private String sign;

    private String inviteCode;

    private Date createTime;

    private Date updateTime;

    private Date lastLoginTime;

    private String memo;

    private Date deactiveTime;

    private String openid;

    private Integer disabled;

    private long coins;      //金币

    private PlayerType playerType;
}