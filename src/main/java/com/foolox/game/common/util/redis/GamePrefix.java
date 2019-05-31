package com.foolox.game.common.util.redis;

/**
 * comment: 玩家状态相关的缓存前缀
 *
 * @author: lipengfei
 * @date: 20/05/2019
 */
public class GamePrefix extends BasePrefix{

    //一天
    private static final int ONE_DAY = 3600*24;

    private GamePrefix(String prefix) {
        super(ONE_DAY,prefix);
    }

    //getGamePlayerCacheBean:
    //进入房间的用户与ClientSession对应关系  key(userId -- clientSession)
//    public static final GamePrefix ROOM_ID_CLIENTSESSION = new GamePrefix("room:userid:clientsession");
    //getGamePlayerCacheBean: roomid - clientsession list
    public static final GamePrefix ROOM_ROOMID_CLIENTSESSION_LIST = new GamePrefix("room:roomid:clientsession_list");

    //getRoomMappingCacheBean
    //进入房间的用户Id 与 gameRoomId的对应关系 key(userId -- gameroomId)
    public static final GamePrefix ROOM_USERID_GAMEROOMID = new GamePrefix("room:userid:roomid");

    //getGameRoomCacheBean
    //RoomId 与 GameRoom 的对应关系 key(RoomId -- GameRoom)
    public static final GamePrefix ROOM_ROOMID_GAMEROOM = new GamePrefix("room:roomid:gameroom");
    //getQueneCache()
    //玩法 与 房间列表 的对应关系，用于匹配房间
    public static final GamePrefix ROOM_PLAYWAY_GAMEROOM_LIST = new GamePrefix("room:playwayId:gameroom_list");

    //getBoardCacheBean roomId -- board
    public static final GamePrefix ROOM_ROOMID_BOARD = new GamePrefix("room:roomid:board");

    //PlayUserESRepository clientsessionid -- gameplayer
//    public static final GamePrefix ROOM_CLIENTSESSIONID_GAMEPLAYER = new GamePrefix("room:clientsessionid:gameplayer");

}
