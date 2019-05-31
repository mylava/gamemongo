package com.foolox.game.common.util.redis;

/**
 * comment:
 *
 * @author: lipengfei
 * @date: 23/08/2018
 * @company: (C) Copyright 58BTC 2018
 * @since: JDK 1.8
 * @description:
 */
public class PlayerPrefix extends BasePrefix{

    //默认token过期时间为2天
    private static final int TOKEN_EXPIRE = 3600*24*2;

    private PlayerPrefix(String prefix) {
        super(TOKEN_EXPIRE,prefix);
    }

    public static final PlayerPrefix TOKEN = new PlayerPrefix("token");

    //getGamePlayerCacheBean:
    //进入房间的用户与ClientSession对应关系  key(userId -- clientSession)
    //getApiUserCacheBean 保存 userId 与 clientsession 的映射关系
    public static final PlayerPrefix PLAYER_ID_CLIENTSESSION = new PlayerPrefix("getGamePlayer:userid:clientsession");
    public static final PlayerPrefix PLAYER_ROOM = new PlayerPrefix("clientSessionList:room");
    public static final PlayerPrefix PLAYER_CLIENTSESSIONID_PLAYER = new PlayerPrefix("getGamePlayer:clientsessionid:getGamePlayer");
}
