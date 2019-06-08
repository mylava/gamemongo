package com.foolox.game.common.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/**
 * comment: 游戏大类，不同的GameModel （如 麻将、跑得快、地主、牛牛等）
 *
 * @author: lipengfei
 * @date: 04/06/2019
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class GameModel {
    private String id;
    private String code;
    private String name;

    private List<GameType> gameTypeList = new ArrayList<GameType>();
}
