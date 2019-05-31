package com.foolox.game.core.engin.game;

import com.foolox.game.common.repo.dao.GameRoomRepository;
import com.foolox.game.common.repo.domain.ClientSession;
import com.foolox.game.common.repo.domain.GamePlayway;
import com.foolox.game.common.repo.domain.GameRoom;
import com.foolox.game.common.util.FooloxGameTaskUtil;
import com.foolox.game.common.util.FooloxUtils;
import com.foolox.game.common.util.GameUtils;
import com.foolox.game.common.util.client.FooloxClientContext;
import com.foolox.game.common.util.redis.GamePrefix;
import com.foolox.game.common.util.redis.RedisService;
import com.foolox.game.common.util.redis.SystemPrefix;
import com.foolox.game.constants.*;
import com.foolox.game.core.FooloxDataContext;
import com.foolox.game.core.engin.game.event.*;
import com.foolox.game.core.engin.game.state.GameEvent;
import com.foolox.game.core.engin.game.state.GameEventType;
import com.foolox.game.core.server.FooloxClient;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.kie.api.runtime.KieSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * comment:
 *
 * @author: lipengfei
 * @date: 20/05/2019
 */
@Slf4j
@Service
public class GameEngine {

    @Autowired
    private RedisService redisService;
    @Resource
    private KieSession kieSession;

    /**
     * 解散房间 , 解散的时候，需要验证下，当前对象是否是房间的创建人
     *
     * @param gameRoom
     * @param userId
     */
    public void dismissRoom(GameRoom gameRoom, String userId) {
        if (gameRoom.getMaster().equals(userId)) {
            log.info("========================dismissRoom========================");
            /*List<ClientSession> maxPlayerNum = redisService.get(GamePrefix.ROOM_ROOMID_GAMEROOM, userId, ClientSession.class);
            for (PlayUserClient clientSessionList : maxPlayerNum) {
                *//**
             * 解散房间的时候，只清理 AI
             *//*
                if (clientSessionList.getPlayerStatus().equals(BMDataContext.PlayerTypeEnum.AI.toString())) {
                    CacheHelper.getGamePlayerCacheBean().delete(clientSessionList.getId(), orgi);
                    CacheHelper.getRoomMappingCacheBean().delete(clientSessionList.getId(), orgi);
                }
            }*/
            /**
             * 先不删
             */
//			UKTools.published(gameRoom, null, BMDataContext.getContext().getBean(GameRoomRepository.class) , BMDataContext.UserDataEventType.DELETE.toString());
        }
    }


    public void gameRequest(ClientSession clientSession, FooloxClient fooloxClient) {
        GameEvent gameEvent = gameRequest(clientSession.getId(), fooloxClient.getPlayway(), fooloxClient, clientSession);
        if (gameEvent != null) {
            /**
             * 举手了，表示游戏可以开始了
             */
            if (clientSession != null) {
                clientSession.setPlayerGameStatus(PlayerGameStatus.READY);
            }

            /**
             * 游戏状态 ， 玩家请求 游戏房间，获得房间状态后，发送事件给 StateMachine，由 StateMachine驱动 游戏状态 ， 此处只负责通知房间内的玩家
             * 1、有新的玩家加入
             * 2、给当前新加入的玩家发送房间中所有玩家信息（不包含隐私信息，根据业务需求，修改ClientSession的字段，剔除掉隐私信息后发送）
             */
            ActionTaskUtils.sendEvent(Command.JOIN_ROOM, new JoinRoom(clientSession, gameEvent.getIndex(), gameEvent.getGameRoom().getMaxPlayerNum(), gameEvent.getGameRoom()), gameEvent.getGameRoom());
            /**
             * 发送给单一玩家的消息
             */
            ActionTaskUtils.sendPlayers(fooloxClient, gameEvent.getGameRoom());
            /**
             * 当前是在游戏中还是 未开始
             */
            Board board = FooloxUtils.getBoardByRoomId(gameEvent.getRoomid(), Board.class);
            if (board != null) {
                GamePlayer currentPlayer = null;
                for (GamePlayer gamePlayer : board.getGamePlayers()) {
                    if (gamePlayer.getPlayuserId().equals(clientSession.getId())) {
                        currentPlayer = gamePlayer;
                        break;
                    }
                }
                if (currentPlayer != null) {
                    boolean automic = false;
                    //（最后出牌不是空 并且 最后出牌玩家是自己） 或 （最后出牌是空 并且 庄家是自己）
                    if ((board.getLast() != null && board.getLast().getUserid().equals(currentPlayer.getPlayuserId())) || (board.getLast() == null && board.getBanker().equals(currentPlayer.getPlayuserId()))) {
                        automic = true;
                    }
                    //恢复牌局信息
                    ActionTaskUtils.sendEvent(Command.RECOVERY, new RecoveryData(currentPlayer, board.getLasthands(),
                            board.getNextplayer() != null ? board.getNextplayer().getNextplayer() : null,
                            25, automic, board), gameEvent.getGameRoom());
                }
            } else {
                //通知状态
                GameUtils.getGame(fooloxClient.getPlayway()).change(gameEvent); //通知状态机 , 此处应由状态机处理异步执行
            }
        }
    }


    /**
     * 玩家房间选择， 新请求，游戏撮合， 如果当前玩家是断线重连， 或者是 退出后进入的，则第一步检查是否已在房间
     * 如果已在房间，直接返回
     *
     * @param userid
     * @param playwayId
     * @param fooloxClient
     * @param clientSession
     * @return
     */
    private GameEvent gameRequest(String userid, String playwayId, FooloxClient fooloxClient, ClientSession clientSession) {
        GameEvent gameEvent = null;
        //通过userId取出roomID
        String roomid = redisService.get(GamePrefix.ROOM_USERID_GAMEROOMID, clientSession.getUserId());
        //从系统配置中取出玩法
        GamePlayway gamePlayway = redisService.get(SystemPrefix.CONFIG_ID_PLAYWAY, playwayId, GamePlayway.class);
        boolean needtakequene = false;
        if (gamePlayway != null) {
            gameEvent = new GameEvent(gamePlayway.getPlayerNum(), gamePlayway.getCardsNum());
            GameRoom gameRoom = null;
            //已存在对应的房间----不用新建
            if (!StringUtils.isBlank(roomid) && redisService.get(GamePrefix.ROOM_ROOMID_GAMEROOM, clientSession.getRoomid(), GameRoom.class) != null) {
                gameRoom = redisService.get(GamePrefix.ROOM_ROOMID_GAMEROOM, clientSession.getRoomid(), GameRoom.class);
            } else {
                //----需要新建房间
                //房卡游戏 , 创建ROOM
                if (RoomType.FEE == fooloxClient.getRoomType()) {
                    gameRoom = this.createGameRoom(gamePlayway, userid, true, fooloxClient);
                } else {
                    /**
                     * 大厅游戏 ， 撮合游戏 , 发送异步消息，通知RingBuffer进行游戏撮合，撮合算法描述如下：
                     * 1、按照玩法查找
                     *
                     */
                    gameRoom = poll(playwayId);
                    if (gameRoom != null) {
                        /**
                         * 修正获取gameroom获取的问题，因为删除房间的时候，为了不损失性能，没有将队列里的房间信息删除，如果有玩家获取到这个垃圾信息
                         * 则立即进行重新获取房间
                         */
                        while (redisService.get(GamePrefix.ROOM_ROOMID_GAMEROOM, gameRoom.getId()) == null) {
                            gameRoom = poll(playwayId);
                            if (gameRoom == null) {
                                break;
                            }
                        }
                    }

                    if (gameRoom == null) {    //无房间 ， 需要
                        gameRoom = this.createGameRoom(gamePlayway, userid, false, fooloxClient);
                    } else {
                        clientSession.setPlayerindex(System.currentTimeMillis());//从后往前坐，房主进入以后优先坐在 首位
                        needtakequene = true;
                    }
                }
            }
            if (gameRoom != null) {
                /**
                 * 设置游戏当前已经进行的局数
                 */
                gameRoom.setCurrentnum(0);
                /**
                 * 更新缓存
                 */
                redisService.set(GamePrefix.ROOM_ROOMID_GAMEROOM, gameRoom.getId(), gameRoom);
                /**
                 * 如果当前房间到达了最大玩家数量，则不再加入到 撮合队列
                 */
                List<ClientSession> haveInRoomPlayerList = FooloxUtils.getRoomClientSessionList(gameRoom.getId());
                if (haveInRoomPlayerList.size() == 0) {
                    gameEvent.setEventType(GameEventType.ENTER);
                } else {
                    gameEvent.setEventType(GameEventType.JOIN);
                }
                gameEvent.setGameRoom(gameRoom);
                gameEvent.setRoomid(gameRoom.getId());

                /**
                 * 加入房间
                 */
                this.joinRoom(gameRoom, clientSession, haveInRoomPlayerList);

                for (ClientSession temp : haveInRoomPlayerList) {
                    if (temp.getId().equals(clientSession.getId())) {
                        gameEvent.setIndex(haveInRoomPlayerList.indexOf(temp));
                        break;
                    }
                }
                /**
                 * 如果当前房间到达了最大玩家数量，则不再加入到 撮合队列
                 */
                if (haveInRoomPlayerList.size() < gamePlayway.getPlayerNum() && needtakequene) {
                    //未达到最大玩家数量，加入到游戏撮合 队列，继续撮合
                    redisService.hset(GamePrefix.ROOM_PLAYWAY_GAMEROOM_LIST, playwayId, roomid, gameRoom);
                }
            }
        }
        return gameEvent;
    }


    /**
     * 创建新房间，需要传入房间的玩法 ， 玩法定义在 系统运营后台，玩法创建后，放入系统缓存，客户端进入房间的时候，传入玩法ID参数
     *
     * @param playway
     * @param userid
     * @param cardroom
     * @param fooloxClient
     * @return
     */
    private GameRoom createGameRoom(GamePlayway playway, String userid, boolean cardroom, FooloxClient fooloxClient) {
        GameRoom gameRoom = new GameRoom();
        gameRoom.setCreatetime(new Date());
        gameRoom.setRoomid(FooloxUtils.getUUID());
        gameRoom.setUpdatetime(new Date());

        if (playway != null) {
            gameRoom.setPlaywayId(playway.getId());
            gameRoom.setRoomtype(playway.getRoomtype());
            gameRoom.setMaxPlayerNum(playway.getPlayerNum());
        }
        gameRoom.setMaxPlayerNum(playway.getPlayerNum());
        gameRoom.setCardsnum(playway.getCardsNum());

        gameRoom.setCurpalyers(1);
        gameRoom.setCardroom(cardroom);
        gameRoom.setStatus(RoomStatus.CRERATED);
        gameRoom.setCardsnum(playway.getCardsNum());
        gameRoom.setCurrentnum(0);
        gameRoom.setCreater(userid);
        gameRoom.setMaster(userid);
        //局数
        gameRoom.setNumofgames(playway.getNumofgames());
        gameRoom.setOrgi(playway.getOrgi());

        /**
         * 房卡模式启动游戏
         */
        if (RoomType.FEE == fooloxClient.getRoomType()) {
            gameRoom.setRoomtype(RoomType.FEE);
            gameRoom.setCardroom(true);
            gameRoom.setExtparams(fooloxClient.getExtparams());
            /**
             * 产生房间ID，麻烦的是需要处理冲突 ，准备采用的算法是 先生成一个号码池子，然后从分布是缓存的 Queue里获取
             */
            gameRoom.setRoomid(FooloxUtils.getRandomNumberChar(6));
            /**
             * 分配房间号码 ， 并且，启用 规则引擎，对房间信息进行赋值
             */
            kieSession.insert(gameRoom);
            //执行规则，如果房间类型为地主类型，则修改变量
            kieSession.fireAllRules();
        } else {
            gameRoom.setRoomtype(RoomType.HALL);
        }

        //未达到最大玩家数量，加入到游戏撮合 队列，继续撮合
        redisService.hset(GamePrefix.ROOM_PLAYWAY_GAMEROOM_LIST, playway.getId(), gameRoom.getId(), gameRoom);
        //保存到数据库
        FooloxUtils.published(gameRoom, FooloxDataContext.getApplicationContext().getBean(GameRoomRepository.class));

        return gameRoom;
    }

    /**
     * 玩家加入房间
     *
     * @param gameRoom
     * @param clientSession
     * @param clientSessionList
     */
    public void joinRoom(GameRoom gameRoom, ClientSession clientSession, List<ClientSession> clientSessionList) {
        boolean inroom = false;
        //已经在房间中的用户
        for (ClientSession session : clientSessionList) {
            if (session.getId().equals(clientSession.getId())) {
                inroom = true;
                break;
            }
        }
        if (!inroom) {
            clientSession.setPlayerindex(System.currentTimeMillis());
            clientSession.setPlayerGameStatus(PlayerGameStatus.READY);
            clientSession.setPlayerStatus(PlayerStatus.NORMAL);
            clientSession.setRoomid(gameRoom.getId());
            clientSession.setRoomready(false);
            clientSessionList.add(clientSession);
            //什么也没做
            FooloxClientContext.getFooloxClientCache().joinRoom(clientSession.getId(), gameRoom.getId());
            //将用户加入到 room
            FooloxUtils.setClientSessionById(clientSession.getUserId(), clientSession);
        }

        /**
         *	不管状态如何，玩家一定会加入到这个房间
         */
        redisService.set(GamePrefix.ROOM_USERID_GAMEROOMID, clientSession.getId(), gameRoom.getId());
    }

    /**
     * 抢庄
     *
     * @param roomid
     * @param orgi
     * @return
     */
    public void actionRequest(String roomid, ClientSession clientSession, String orgi, boolean accept) {
        GameRoom gameRoom = redisService.get(GamePrefix.ROOM_ROOMID_GAMEROOM, roomid, GameRoom.class);
        if (gameRoom != null) {
            DiZhuBoard board = FooloxUtils.getBoardByRoomId(gameRoom.getId(), DiZhuBoard.class);
            GamePlayer player = board.getGamePlayer(clientSession.getId());
            board = ActionTaskUtils.doCatch(board, player, accept);

            ActionTaskUtils.sendEvent(Command.CATCH_RESULT, new GameBoard(player.getPlayuserId(), player.isAccept(), board.isDocatch(), board.getRatio()), gameRoom);
            GameUtils.getGame(gameRoom.getPlaywayId()).change(gameRoom, GameEventType.AUTO.toString(), 15);    //通知状态机 , 继续执行
            //更新board 信息到缓存
            FooloxUtils.setBoardByRoomId(gameRoom.getId(), board);
            //1秒后开始执行任务
            FooloxGameTaskUtil.getExpireCache().put(gameRoom.getRoomid(), ActionTaskUtils.createAutoTask(1, gameRoom));
        }
    }


    /**
     * 根据玩法，取出空闲房间
     *
     * @return
     */
    private GameRoom poll(String playwayId) {
        GameRoom gameRoom = null;
        Map<String, GameRoom> map = redisService.hgetAll(GamePrefix.ROOM_PLAYWAY_GAMEROOM_LIST, playwayId, GameRoom.class);
        for (String s : map.keySet()) {
            GameRoom room = map.get(s);
            redisService.hdel(GamePrefix.ROOM_PLAYWAY_GAMEROOM_LIST, playwayId, room.getId());
            List<ClientSession> clientSessions = FooloxUtils.getRoomClientSessionList(room.getId());
            if (clientSessions.size() < room.getMaxPlayerNum()) {
                gameRoom = room;
                break;
            }
        }
        return gameRoom;
    }

}
