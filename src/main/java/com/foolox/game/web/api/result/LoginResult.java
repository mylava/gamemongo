package com.foolox.game.web.api.result;

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
    //    private Token token ;
    private String token;
    private List<SystemDict> games;
    private ClientSession clientSession;
}
