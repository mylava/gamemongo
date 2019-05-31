package com.foolox.game.common.model;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * comment:
 *
 * @author: lipengfei
 * @date: 16/05/2019
 */
@Data
public class GameType {
    private String id ;
    private String name ;
    private String code ;
    //场次相关配置，如 低中高级场入场限制等
    private List<Playway> playways = new ArrayList<Playway>();
}
