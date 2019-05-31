package com.foolox.game.core.engin.game;

import com.foolox.game.common.model.GameType;
import com.foolox.game.common.repo.domain.GamePlayway;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * comment: 游戏类型，如：麻将、地主、牛牛
 *
 * @author: lipengfei
 * @date: 11/05/2019
 */
@Data
public class FooloxGame {

    private String id;
    private String code;
    private String name;
    //游戏类型的细分：如血战、妖姬、无花牛牛等
    private List<GameType> gameTypes = new ArrayList<GameType>();
}
