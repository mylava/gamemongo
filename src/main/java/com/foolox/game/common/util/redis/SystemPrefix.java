package com.foolox.game.common.util.redis;

/**
 * comment:
 *
 * @author: lipengfei
 * @date: 16/05/2019
 */
public class SystemPrefix extends BasePrefix{
    private SystemPrefix(String prefix) {
        super(prefix);
    }

    //系统字典
    public static final SystemPrefix CONFIG = new SystemPrefix("config");

    public static final SystemPrefix GAMES = new SystemPrefix("games");
    
    /**
     * --------------- ---------------
     * 上边需要重新上设计
     * --------------- ---------------
     */
    public static final SystemPrefix CONFIG_ID_PLAYWAY = new SystemPrefix("config:playwayId");

    // gameId -- sysdic
    public static final SystemPrefix CONFIG_ID_SYSDIC = new SystemPrefix("config:sysdic");
    
    
}
