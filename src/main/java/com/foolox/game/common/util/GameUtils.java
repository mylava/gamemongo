package com.foolox.game.common.util;

import com.foolox.game.common.model.Game;
import com.foolox.game.common.repo.domain.GamePlayway;
import com.foolox.game.common.repo.domain.GameRoom;
import com.foolox.game.common.repo.domain.SysDic;
import com.foolox.game.common.util.redis.GamePrefix;
import com.foolox.game.common.util.redis.RedisService;
import com.foolox.game.common.util.redis.SystemPrefix;
import com.foolox.game.constants.PlayerGameStatus;
import com.foolox.game.core.FooloxDataContext;
import com.foolox.game.common.repo.domain.ClientSession;
import org.apache.commons.lang3.StringUtils;

/**
 * comment:
 *
 * @author: lipengfei
 * @date: 19/05/2019
 */
public class GameUtils {

/*    private static Map<String,ChessGame> games = new HashMap<String,ChessGame>();
    static{
        games.put("dizhu", new DizhuGame()) ;
        games.put("majiang", new MaJiangGame()) ;
    }*/


    private static RedisService redisService = FooloxDataContext.getApplicationContext().getBean(RedisService.class);

    /**
     * 如果不在游戏中，更新缓存，并解散房间
     *
     * @param clientSession
     */
    public static void syncNotPlaying(ClientSession clientSession) {
        if (clientSession != null && PlayerGameStatus.PLAYING!=clientSession.getPlayerGameStatus()) {
            //取出已经在房间的 ClientSession
            clientSession = FooloxUtils.getClientSessionById(clientSession.getUserId());
            //删除已在房间的 ClientSession
            FooloxUtils.delClientSessionById(clientSession.getUserId());
            //删除 userId 与 RoomId 的关联关系
            redisService.del(GamePrefix.ROOM_USERID_GAMEROOMID, clientSession.getUserId());

            /**
             * 检查，如果房间没 真人玩家了或者当前玩家是房主 ，就可以解散房间了
             */
            if (clientSession != null && !StringUtils.isBlank(clientSession.getRoomid())) {
                //取出GameRoom
                GameRoom gameRoom = redisService.get(GamePrefix.ROOM_ROOMID_GAMEROOM, clientSession.getRoomid(), GameRoom.class);
                //是自己创建的房间，则解散
                if (gameRoom.getMaster().equals(clientSession.getId())) {
                    /**
                     * 解散房间，应该需要一个专门的 方法来处理，别直接删缓存了，这样不好！！！
                     */
//                    FooloxDataContext.getGameEngine().dismissRoom(gameRoom, clientSession.getUserId());
                }
            }
        }
    }

    /**
     * 通过playway 获取具体玩法的状态机
     * @param playwayId
     * @return
     */
    public static Game getGame(String playwayId){
        GamePlayway gamePlayway = redisService.get(SystemPrefix.CONFIG_ID_PLAYWAY, playwayId, GamePlayway.class);
        Game game = null ;
        if(gamePlayway!=null){
            SysDic dic = redisService.get(SystemPrefix.CONFIG_ID_SYSDIC, gamePlayway.getGame(), SysDic.class) ;
            if(dic.getCode().equals("dizhu") || gamePlayway.getCode().equals("dizhu")){
                game = FooloxDataContext.getApplicationContext().getBean("dizhuGame", Game.class) ;
            }else if(dic.getCode().equals("majiang") || gamePlayway.getCode().equals("majiang")){
                game = FooloxDataContext.getApplicationContext().getBean("majiangGame", Game.class) ;
            }
        }
        return game;
    }
}
