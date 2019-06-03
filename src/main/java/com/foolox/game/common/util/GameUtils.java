package com.foolox.game.common.util;

import com.foolox.game.common.model.Game;
import com.foolox.game.common.repo.domain.*;
import com.foolox.game.common.util.redis.RedisService;
import com.foolox.game.common.util.redis.SystemPrefix;
import com.foolox.game.constants.PlayerGameStatus;
import com.foolox.game.constants.PlayerStatus;
import com.foolox.game.core.FooloxDataContext;
import com.foolox.game.core.engin.game.Message;
import com.foolox.game.core.server.FooloxClient;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import sun.misc.BASE64Encoder;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.InvocationTargetException;
import java.util.Date;
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
        //玩家不在游戏中[可能要根据游戏决定是否删除房间]
        if (clientSession != null && PlayerGameStatus.PLAYING != clientSession.getPlayerGameStatus()) {
            //拿到 ClientSession
            clientSession = FooloxUtils.getClientSessionById(clientSession.getUserId());
            if (!StringUtils.isBlank(clientSession.getRoomId())) {
                //删除 userId 与 RoomId 的关联关系
                FooloxUtils.delRoomIdByUserId(clientSession.getUserId());
                //从 roomId-ClientSessionList 中删除 ClientSession
                FooloxUtils.removeOneFromRoomClientSession(clientSession.getRoomId(), clientSession.getUserId());
            }

            /**
             * 检查，如果房间没 真人玩家了或者当前玩家是房主 ，就可以解散房间了
             */
            if (null != clientSession && !StringUtils.isBlank(clientSession.getRoomId())) {
                //取出GameRoom
                GameRoom gameRoom = FooloxUtils.getRoomById(clientSession.getRoomId());
                //是自己创建的房间，则解散
                if (gameRoom.getMaster().equals(clientSession.getId())) {
                    /**
                     * 解散房间，应该需要一个专门的 方法来处理，别直接删缓存了，这样不好！！！
                     */
                    FooloxDataContext.getGameEngine().dismissRoom(gameRoom, clientSession.getUserId());
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
     * 创建一个AI玩家
     *
     * @param player
     * @return
     * @throws InvocationTargetException
     * @throws IllegalAccessException
     */
    public static ClientSession createAI(Player player, String playwayId, PlayerStatus playertype) {
        ClientSession clientSession = null;
        if (player != null) {
            if (StringUtils.isBlank(player.getUsername())) {
                player.setUsername("Guest_" + new BASE64Encoder().encode(FooloxUtils.getUUID().toLowerCase().getBytes()));
            }
            if (!StringUtils.isBlank(player.getPassword())) {
                player.setPassword(FooloxUtils.md5(player.getPassword()));
            } else {
                player.setPassword(FooloxUtils.md5(FooloxUtils.getRandomNumberChar(6)));//随机生成一个6位数的密码 ，备用
            }
            player.setPlayerStatus(playertype);    //玩家类型
            player.setCreateTime(new Date());
            player.setUpdateTime(new Date());
            player.setLastLoginTime(new Date());

            AiConfig aiConfig = FooloxUtils.getAiConfigByPlaywayId(playwayId);

            if (PlayerStatus.AI==playertype && aiConfig != null) {
                player.setCoins(aiConfig.getInitcoins());
            } else {
//                AccountConfig config = CacheConfigTools.getGameAccountConfig(BMDataContext.SYSTEM_ORGI);
//                if (config != null) {
//                    player.setGoldcoins(config.getInitcoins());
//                    player.setCards(config.getInitcards());
//                    player.setDiamonds(config.getInitdiamonds());
//                }
            }

            if (!StringUtils.isBlank(player.getId())) {
                clientSession = new ClientSession();
                BeanUtils.copyProperties(clientSession, player);
            }
        }
        return clientSession;
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
