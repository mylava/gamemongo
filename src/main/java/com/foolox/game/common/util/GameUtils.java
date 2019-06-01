package com.foolox.game.common.util;

import com.foolox.game.common.model.Game;
import com.foolox.game.common.repo.domain.GamePlayway;
import com.foolox.game.common.repo.domain.GameRoom;
import com.foolox.game.common.repo.domain.SysDic;
import com.foolox.game.common.util.redis.RedisService;
import com.foolox.game.common.util.redis.SystemPrefix;
import com.foolox.game.constants.PlayerGameStatus;
import com.foolox.game.constants.PlayerStatus;
import com.foolox.game.core.FooloxDataContext;
import com.foolox.game.common.repo.domain.ClientSession;
import com.foolox.game.core.engin.game.Message;
import com.foolox.game.core.engin.game.pva.PVAOperatorResult;
import com.foolox.game.core.server.FooloxClient;
import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.Map;

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
        if (clientSession != null && PlayerGameStatus.PLAYING != clientSession.getPlayerGameStatus()) {
            //取出已经在房间的 ClientSession
            clientSession = FooloxUtils.getClientSessionById(clientSession.getUserId());
            //删除已在房间的 ClientSession
            FooloxUtils.delClientSessionById(clientSession.getUserId());
            //删除 userId 与 RoomId 的关联关系
            FooloxUtils.delRoomIdByUserId(clientSession.getUserId());

            /**
             * 检查，如果房间没 真人玩家了或者当前玩家是房主 ，就可以解散房间了
             */
            if (clientSession != null && !StringUtils.isBlank(clientSession.getRoomId())) {
                //取出GameRoom
                GameRoom gameRoom = FooloxUtils.getRoomById(clientSession.getRoomId());
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
     *
     * @param playwayId
     * @return
     */
    public static Game getGame(String playwayId) {
        GamePlayway gamePlayway = FooloxUtils.getGamePlaywayById(playwayId);
        Game game = null;
        if (gamePlayway != null) {
            SysDic dic = redisService.get(SystemPrefix.CONFIG_ID_SYSDIC, gamePlayway.getGame(), SysDic.class);
            if (dic.getCode().equals("dizhu") || gamePlayway.getCode().equals("dizhu")) {
                game = FooloxDataContext.getApplicationContext().getBean("dizhuGame", Game.class);
            } else if (dic.getCode().equals("majiang") || gamePlayway.getCode().equals("majiang")) {
                game = FooloxDataContext.getApplicationContext().getBean("majiangGame", Game.class);
            }
        }
        return game;
    }

    /**
     * 更新玩家状态
     *
     * @param userId
     * @param status
     */
    public static void updatePlayerClientStatus(String userId, PlayerStatus status) {
        ClientSession clientSession = FooloxUtils.getClientSessionById(userId);
        if (clientSession != null) {
            clientSession.setPlayerStatus(status);//托管玩家
            FooloxUtils.setClientSessionById(userId, clientSession);

            if (null != clientSession && PlayerGameStatus.PLAYING != clientSession.getPlayerGameStatus()) {
                clientSession = FooloxUtils.getClientSessionById(userId);
                FooloxUtils.delClientSessionById(userId);
                FooloxUtils.delRoomIdByUserId(userId);

                /**
                 * 检查，如果房间没 真人玩家了或者当前玩家是房主 ，就可以解散房间了
                 */
                if (clientSession != null && !StringUtils.isBlank(clientSession.getRoomId())) {
                    GameRoom gameRoom = FooloxUtils.getRoomById(clientSession.getRoomId());
                    if (gameRoom.getMaster().equals(clientSession.getId())) {
                        /**
                         * 解散房间，应该需要一个专门的 方法来处理，别直接删缓存了，这样不好！！！
                         */
                        FooloxDataContext.getGameEngine().dismissRoom(gameRoom, userId);
                    }
                }
            }
        }
    }

    /**
     * 破产补助
     *
     * @param client
     * @param playUser
     * @return
     */
    public static Message subsidy(FooloxClient client, ClientSession playUser) {
        //暂不实现
        return null;
    }

    /**
     * --------------- ---------------
     * 以下代码重构时换到对应的logic实现类
     * --------------- ---------------
     */

    /**
     * 定缺方法，计算最少的牌
     *
     * @param cards
     * @return
     */
    public static int selectColor(byte[] cards) {
        Map<Integer, Byte> data = new HashMap<Integer, Byte>();
        for (byte temp : cards) {
            int key = temp / 36;                //花色
            if (data.get(key) == null) {
                data.put(key, (byte) 1);
            } else {
                data.put(key, (byte) (data.get(key) + 1));
            }
        }
        int color = 0, cardsNum = 0;
        if (data.get(0) != null) {
            cardsNum = data.get(0);
            if (data.get(1) == null) {
                color = 1;
            } else {
                if (data.get(1) < cardsNum) {
                    cardsNum = data.get(1);
                    color = 1;
                }
                if (data.get(2) == null) {
                    color = 2;
                } else {
                    if (data.get(2) < cardsNum) {
                        cardsNum = data.get(2);
                        color = 2;
                    }
                }
            }
        }
        return color;
    }

    /**
     * 麻将的出牌判断，杠碰吃胡
     *
     * @param cards
     * @return
     */
    public static Byte getGangCard(byte[] cards) {
        Byte card = null;
        Map<Integer, Byte> data = new HashMap<Integer, Byte>();
        for (byte temp : cards) {
            int value = (temp % 36) / 4;            //牌面值
            int rote = temp / 36;                //花色
            int key = value + 9 * rote;        //
            if (data.get(key) == null) {
                data.put(key, (byte) 1);
            } else {
                data.put(key, (byte) (data.get(key) + 1));
            }
            if (data.get(key) == 4) {    //自己发牌的时候，需要先判断是否有杠牌
                card = temp;
                break;
            }
        }

        return card;
    }
}
