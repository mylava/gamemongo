package com.foolox.game.web.api.user;

import com.foolox.game.common.repo.domain.Player;
import com.foolox.game.common.repo.domain.SystemDict;
import com.foolox.game.common.result.CodeMessage;
import com.foolox.game.common.result.Result;
import com.foolox.game.common.service.PlayerService;
import com.foolox.game.common.service.SystemDictService;
import com.foolox.game.common.util.FooloxUtils;
import com.foolox.game.common.util.JwtUtils;
import com.foolox.game.common.util.redis.PlayerPrefix;
import com.foolox.game.common.util.redis.RedisService;
import com.foolox.game.constants.DictType;
import com.foolox.game.constants.SystemConstant;
import com.foolox.game.common.repo.domain.ClientSession;
import com.foolox.game.web.api.result.LoginResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * comment: 玩家相关的controller
 *
 * @author: lipengfei
 * @date: 11/05/2019
 */
@RestController
@RequestMapping("/api/player")
public class PlayerController {

    @Autowired
    private PlayerService playerService;

    @Autowired
    private RedisService redisService;

    @Autowired
    private SystemDictService systemDictService;

    @RequestMapping("register")
    public Result<LoginResult> register(@RequestBody Player player) {
        return null;
    }


    @RequestMapping("login")
    public Result<LoginResult> login(@RequestBody Player player) {
        ClientSession clientSession = null;

        Player p = playerService.findPlayerByUsername(player.getUsername());
        //不存在的用户
        if (p == null) {
            return Result.fail(CodeMessage.LOGIN_USER_NOT_EXIST);
        }
        //密码错误
        if (!p.getPassword().equals(player.getPassword())) {
            return Result.fail(CodeMessage.LOGIN_PASSWORD_INCORRECT);
        }
        //用户被冻结
        if (p.getState()==0) {
            return Result.fail(CodeMessage.LOGIN_STATE_INCORRECT);
        }

        String token = JwtUtils.createJWT(player);

        LoginResult data = new LoginResult();
        data.setToken(token);

        //token放入缓存
        redisService.set(PlayerPrefix.TOKEN, player.getId(), token);

        //生成clientSession
        clientSession = new ClientSession();
        clientSession.setId(player.getId());
        clientSession.setUserId(player.getId());
        clientSession.setToken(token);
        clientSession.setUsername(player.getUsername());
        clientSession.setPassword(FooloxUtils.md5(player.getPassword()));
        clientSession.setLogin(true);
        clientSession.setOnline(false);
        clientSession.setHeadimg(player.getHeadimg());

        clientSession.setOrgi(SystemConstant.SYSTEM_ORGI);
        //保存clientSession到缓存
        FooloxUtils.setClientSessionById(player.getId(), clientSession);

        data.setClientSession(clientSession);

        //STAY 实现route功能
        //STAY 读取系统配置信息，如 游戏模式(大厅、房卡)、房间等待时长、活动信息等

        //STAY 读取游戏配置：模式和玩法等，如果有多个，进入大厅，如果只有一个，进入选场（如低中高级场）
        data.setGames(getGamesConfig(clientSession.getOrgi()));
        //STAY 读取AI配置

        return Result.success(data);
    }



    /**
     * 读取运营商的游戏及配置信息
     * @param code
     * @return
     */
    private List<SystemDict> getGamesConfig(String code) {
        SystemDict orgi = systemDictService.findOneByCodeType(code, DictType.GAME_CONFIG);
        if (null!=orgi) {
            return orgi.getDicts();
        }
        return null;
    }


}
