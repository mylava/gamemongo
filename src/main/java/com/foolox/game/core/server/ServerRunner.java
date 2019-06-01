package com.foolox.game.core.server;

import com.foolox.game.common.repo.domain.SystemDict;
import com.foolox.game.common.service.SystemDictService;
import com.foolox.game.common.util.redis.RedisService;
import com.foolox.game.common.util.redis.SystemPrefix;
import com.foolox.game.constants.DictType;
import com.foolox.game.constants.SystemConstant;
import com.foolox.game.core.FooloxDataContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * comment: 服务启动时，启动 socket server
 *
 * @author: lipengfei
 * @date: 06/05/2019
 */
@Component
public class ServerRunner implements CommandLineRunner {

    @Autowired
    private SystemDictService systemDictService;

    @Autowired
    private RedisService redisService;

    private final GameServer server;

    @Autowired
    public ServerRunner(GameServer server) {
        this.server = server;
    }


    private void initGame() {
        //租户信息
        SystemDict orgi = systemDictService.findOneByCodeType(SystemConstant.SYSTEM_ORGI, DictType.GAME_CONFIG);
        //更新配置信息到缓存中
        refresh(orgi.getDicts());
    }

    @Override
    public void run(String... args) throws Exception {
        initGame();
        server.start();
        FooloxDataContext.setServerRunning(true);
    }

    /**
     * 递归配置信息到缓存
     * @param dicts
     */
    private void refresh(List<SystemDict> dicts) {
        //初始化游戏到缓存中
        for (SystemDict dict : dicts) {
            redisService.set(SystemPrefix.CONFIG, dict.getCode(),  dict.getConfig());
            refresh(dict.getDicts());
        }
    }
}
