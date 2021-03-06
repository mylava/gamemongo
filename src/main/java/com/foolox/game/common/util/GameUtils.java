package com.foolox.game.common.util;

import com.foolox.game.core.statemachine.Game;
import com.foolox.game.common.repo.domain.*;
import com.foolox.game.common.util.redis.RedisService;
import com.foolox.game.common.util.redis.SystemPrefix;
import com.foolox.game.constants.PlayerGameStatus;
import com.foolox.game.constants.PlayerType;
import com.foolox.game.core.FooloxDataContext;
import com.foolox.game.core.FooloxGameProcessor;
import com.foolox.game.core.engin.game.Message;
import com.foolox.game.core.engin.game.event.Board;
import com.foolox.game.core.logic.DizhuGameProcessor;
import com.foolox.game.core.logic.MajiangGameProcessor;
import com.foolox.game.core.server.FooloxClient;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import sun.misc.BASE64Encoder;

import java.lang.reflect.InvocationTargetException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * comment:
 *
 * @author: lipengfei
 * @date: 19/05/2019
 */
public class GameUtils {

    private static Map<String, FooloxGameProcessor> games = new HashMap<String, FooloxGameProcessor>();

    static {
        games.put("dizhu", new DizhuGameProcessor());
        games.put("majiang", new MajiangGameProcessor());
    }


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
                FooloxUtils.removeSessionFromRoom(clientSession.getRoomId(), clientSession.getUserId());
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
            if (gamePlayway.getModelCode().equals("dizhu")) {
                game = FooloxDataContext.getApplicationContext().getBean("dizhuGame", Game.class);
            } else if (gamePlayway.getModelCode().equals("majiang")) {
                game = FooloxDataContext.getApplicationContext().getBean("majiangGame", Game.class);
            }
        }
        return game;
    }

    /**
     * 更新玩家状态
     *
     * @param userId
     * @param playerType
     */
    public static void updatePlayerClientStatus(String userId, PlayerType playerType) {
        ClientSession clientSession = FooloxUtils.getClientSessionById(userId);
        if (clientSession != null) {
            clientSession.setPlayerType(playerType);//托管玩家
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
    public static ClientSession createAI(Player player, String playwayId, PlayerType playertype) {
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
            player.setPlayerType(playertype);    //玩家类型
            player.setCreateTime(new Date());
            player.setUpdateTime(new Date());
            player.setLastLoginTime(new Date());

            AiConfig aiConfig = FooloxUtils.getAiConfigByPlaywayId(playwayId);

            if (PlayerType.AI == playertype && aiConfig != null) {
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
     * 开始游戏，根据玩法创建游戏 对局
     *
     * @return
     */
    public static Board playGame(List<ClientSession> clientSessionList, GameRoom gameRoom, String banker, int cardsnum) {
        Board board = null;
        GamePlayway gamePlayWay = FooloxUtils.getGamePlaywayById(gameRoom.getPlaywayId());
        if (gamePlayWay != null) {
            FooloxGameProcessor fooloxGameProcessor = games.get(gamePlayWay.getModelCode());
            if (fooloxGameProcessor != null) {
                board = fooloxGameProcessor.process(clientSessionList, gameRoom, gamePlayWay, banker, cardsnum);
            }
        }
        return board;
    }

    /**
     * 反转指定的 cards 数组
     *
     * @param cards
     * @return
     */
    public static byte[] reverseCards(byte[] cards) {
        byte[] target_cards = new byte[cards.length];
        for (int i = 0; i < cards.length; i++) {
            // 反转后数组的第一个元素等于源数组的最后一个元素：
            target_cards[i] = cards[cards.length - i - 1];
        }
        return target_cards;
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
