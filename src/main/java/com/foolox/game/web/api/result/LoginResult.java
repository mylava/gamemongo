package com.foolox.game.web.api.result;

import com.foolox.game.common.model.GameModel;
import com.foolox.game.common.repo.domain.SystemDict;
import com.foolox.game.common.repo.domain.ClientSession;
import lombok.Data;

import java.util.List;

/**
 * comment:
 *
 * @author: lipengfei
 * @date: 11/05/2019
 */
@Data
public class LoginResult {
    private String token;
    private List<GameModel> games;
    private ClientSession clientSession;
}
