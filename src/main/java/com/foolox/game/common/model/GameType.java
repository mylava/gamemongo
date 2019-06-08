package com.foolox.game.common.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/**
 * comment: 游戏小类 (二人、三人等等)
 *
 * @author: lipengfei
 * @date: 16/05/2019
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class GameType {
    private String id;
    private String name;
    private String code;
    //GameModel.code
    private String modelCode; //游戏类型 ： 麻将：地主：德州
    //场次相关配置，如 低中高级场入场限制等
    private List<Playway> playwayList = new ArrayList<Playway>();
}
