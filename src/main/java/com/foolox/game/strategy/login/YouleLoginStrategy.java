package com.foolox.game.strategy.login;

import com.foolox.game.common.repo.domain.ClientSession;
import com.foolox.game.common.util.FooloxUtils;
import com.foolox.game.core.FooloxDataContext;
import com.foolox.game.strategy.Organization;

/**
 * comment:
 *
 * @author: lipengfei
 * @date: 25/06/2019
 */
@Organization(name = "youle")
public class YouleLoginStrategy implements LoginStrategy{
    @Override
    public LoginResult getLoginResult(String token, ClientSession clientSession) {
        YouleLoginResult loginResult = new YouleLoginResult();
        loginResult.setToken(token);
        loginResult.setClientSession(clientSession);

        //STAY 读取游戏配置：模式和玩法等，如果有多个，进入大厅，如果只有一个，进入选场（如低中高级场）
        loginResult.setGames(FooloxUtils.getGamesByOrg(FooloxDataContext.DIC_ORG));
        return loginResult;
    }
}
