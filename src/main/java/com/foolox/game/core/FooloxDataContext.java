package com.foolox.game.core;

import com.foolox.game.core.engin.game.GameEngine;
import org.springframework.context.ApplicationContext;

/**
 * comment: 数据上下文
 *
 * @author: lipengfei
 * @date: 06/05/2019
 */
public class FooloxDataContext {
    //socket server状态
    private static boolean serverRunning = false;
    //系统启动时，将spring容器注入进来，getBean时使用
    private static ApplicationContext applicationContext;
    //游戏引擎
    private static GameEngine gameEngine;

    //与客户端交互使用的枚举
    public static final String FOOLOX_MESSAGE_EVENT = "command";
    public static final String FOOLOX_PLAYERS_EVENT = "players";
    public static final String FOOLOX_GAMESTATUS_EVENT = "gamestatus";
    public static final String FOOLOX_SEARCHROOM_EVENT = "searchroom";

    //系统字典表code
    public static final String DIC_ORGI = "foolox";

    public static ApplicationContext getApplicationContext() {
        return applicationContext;
    }

    public static void setApplicationContext(ApplicationContext applicationContext) {
        FooloxDataContext.applicationContext = applicationContext;
    }

    public static boolean isServerRunning() {
        return serverRunning;
    }

    public static void setServerRunning(boolean serverRunning) {
        FooloxDataContext.serverRunning = serverRunning;
    }

    public static GameEngine getGameEngine() {
        return gameEngine;
    }

    public static void setGameEngine(GameEngine engine) {
        gameEngine = engine;
    }
}
