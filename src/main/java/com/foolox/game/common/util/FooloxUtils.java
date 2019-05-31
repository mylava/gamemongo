package com.foolox.game.common.util;

import com.foolox.game.common.repo.domain.ClientSession;
import com.foolox.game.common.repo.domain.Player;
import com.foolox.game.common.util.event.UserDataEvent;
import com.foolox.game.common.util.event.UserDataEventType;
import com.foolox.game.common.util.event.UserEvent;
import com.foolox.game.common.util.redis.GamePrefix;
import com.foolox.game.common.util.redis.PlayerPrefix;
import com.foolox.game.common.util.redis.RedisService;
import com.foolox.game.core.FooloxDataContext;
import com.foolox.game.core.engin.game.event.Board;
import com.foolox.game.core.engin.game.event.GamePlayer;
import com.lmax.disruptor.dsl.Disruptor;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.UUID;

/**
 * comment:
 *
 * @author: lipengfei
 * @date: 12/05/2019
 */
public class FooloxUtils {

    private static MD5 md5 = new MD5();

    public static String getUUID() {
        return UUID.randomUUID().toString().replace("-", "");
    }

    public static String md5(String str) {
        return md5.getMD5ofStr(md5.getMD5ofStr(str));
    }

    private static RedisService redisService = FooloxDataContext.getApplicationContext().getBean(RedisService.class);

    /**
     * 异步保存UserEvent 到数据库
     * @param event
     * @param mongoRepository
     */
    public static void published(UserEvent event, MongoRepository mongoRepository) {
        published(event, mongoRepository, UserDataEventType.SAVE);
    }

    /**
     * 异步操作UserEvent数据库
     * @param event
     * @param mongoRepository
     * @param command
     */
    @SuppressWarnings({"rawtypes", "unchecked"})
    private static void published(UserEvent event, MongoRepository mongoRepository, UserDataEventType command) {
        Disruptor<UserDataEvent> disruptor = (Disruptor<UserDataEvent>) FooloxDataContext.getApplicationContext().getBean("disruptor");
        long seq = disruptor.getRingBuffer().next();
        UserDataEvent userDataEvent = disruptor.getRingBuffer().get(seq);
        userDataEvent.setEvent(event);
        userDataEvent.setRepository(mongoRepository);
        userDataEvent.setCommand(command);
        disruptor.getRingBuffer().publish(seq);
    }

    /**
     * 生成length长度的随机数
     * @param length
     * @return
     */
    public static String getRandomNumberChar(int length) {
        if (length>1) {
            double random = (Math.random() * 9 + 1);
            int pow = (int) Math.pow(10d, length);
            return String.valueOf((int)random*pow);
        }
        return null;
    }


    /**
     * 通过 userId 获取 ClientSession
     * @param userId
     * @return
     */
    public static ClientSession getClientSessionById(String userId) {
        return redisService.get(PlayerPrefix.PLAYER_ID_CLIENTSESSION, userId, ClientSession.class);
    }
    /**
     * 保存 UserId 与 ClientSession 映射关系到缓存
     * @param userId
     */
    public static void setClientSessionById(String userId, ClientSession clientSession) {
        redisService.set(PlayerPrefix.PLAYER_ID_CLIENTSESSION, userId, clientSession);
    }

    /**
     * 从缓存中删除 UserId 与 ClientSession 映射关系
     * @param userId
     */
    public static void delClientSessionById(String userId) {
        redisService.del(PlayerPrefix.PLAYER_ID_CLIENTSESSION, userId);
    }

    /**
     * 通过 roomId 获取 Board
     * @param roomId
     * @param clazz
     * @param <T>
     * @return
     */
    public static <T extends Board> T getBoardByRoomId(String roomId, Class<T> clazz) {
        return redisService.get(GamePrefix.ROOM_ROOMID_BOARD, roomId, clazz);
    }

    /**
     * 保存 roomId 与 Board 映射关系到缓存
     * @param roomId
     * @param board
     * @return
     */
    public static void setBoardByRoomId(String roomId, Board board) {
        redisService.set(GamePrefix.ROOM_ROOMID_BOARD, roomId, board);
    }

    public static List<ClientSession> getRoomClientSessionList(String roomId) {
        return redisService.lrange(GamePrefix.ROOM_ROOMID_CLIENTSESSION_LIST, roomId, 0, -1, ClientSession.class);
    }

/*    public static Player getGameplayerBySessionId(String clientSessionId) {
        return redisService.get(GamePrefix.ROOM_CLIENTSESSIONID_GAMEPLAYER, clientSessionId, GamePlayer.class);
    }*/

    /**
     * 通过 clientSessionId 获取 Player
     * 如果获取不到，则是机器人
     * @param clientSessionId
     * @return
     */
    public static Player getPlayerBySessionId(String clientSessionId) {
        return redisService.get(PlayerPrefix.PLAYER_CLIENTSESSIONID_PLAYER, clientSessionId, Player.class);
    }



}
