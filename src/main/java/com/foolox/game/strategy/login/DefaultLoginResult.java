package com.foolox.game.strategy.login;

import com.foolox.game.common.model.GameModel;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * comment:
 *
 * @author: lipengfei
 * @date: 25/06/2019
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class DefaultLoginResult extends LoginResult {
    private List<GameModel> games;
}
