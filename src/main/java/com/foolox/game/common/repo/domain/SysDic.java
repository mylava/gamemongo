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
    private String title = "pub";
    private String code;
    private String orgi;
    private String ctype;
    private String parentid;
    private String iconstr;
    private String iconskin;
    private String description;
    private String catetype;
    private String memo;
    private String creater;
    private boolean haschild;                //修改用处，改为启用还是禁用了
    private boolean discode;                //是否显示code
    private Date createtime;
    private Date updatetime;
    private int sortindex;
    private String dicid;

    private String menutype;                //菜单类型，顶部导航菜单， 左侧菜单

    private String rules;                    //角色要求

    private String module;
    private String url;
    private String mlevel;                    //菜单级别， 一级 菜单， 二级菜单

    private boolean defaultvalue = false;
}
