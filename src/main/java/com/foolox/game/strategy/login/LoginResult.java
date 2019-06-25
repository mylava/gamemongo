package com.foolox.game.strategy.login;

import com.foolox.game.common.repo.domain.ClientSession;
import lombok.Data;

/**
 * comment:
 *
 * @author: lipengfei
 * @date: 25/06/2019
 */
@Data
public class LoginResult {
    private String token;
    private ClientSession clientSession;
}
