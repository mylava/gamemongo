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
 * @date: 30/05/2019
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "sysdic")
public class SysDic {
    @Id
    private String id;
    private String name;
    private String title;
    private String code;
    private String parentId;
    private String iconUrl;
    private String description;
    private String memo;
    private String creater;
    private Boolean enable;                //启用还是禁用 暂时无用
    private Date createtime;
    private Date updatetime;
    private Integer sortIndex;

    private String menutype;                //菜单类型，顶部导航菜单， 左侧菜单

    private String rules;                    //角色要求

    private String url;
    private String level;                    //菜单级别， 一级 菜单， 二级菜单

    private Boolean defaultvalue = false;
}
