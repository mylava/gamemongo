package com.foolox.game.strategy.login;

import com.foolox.game.common.repo.domain.ClientSession;

/**
 * comment: 登录后读取大厅信息策略接口
 *
 * @author: lipengfei
 * @date: 25/06/2019
 */
public interface LoginStrategy {
    LoginResult getLoginResult(String token, ClientSession clientSession);
}
