package com.foolox.game.core.server;

import com.alibaba.fastjson.JSON;
import com.foolox.game.common.repo.dao.ClientSessionRepository;
import com.foolox.game.common.repo.domain.GamePlayway;
import com.foolox.game.common.repo.domain.GameRoom;
import com.foolox.game.common.util.FooloxUtils;
import com.foolox.game.common.util.GameUtils;
import com.foolox.game.common.util.client.FooloxClientContext;
import com.foolox.game.common.util.redis.GamePrefix;
import com.foolox.game.common.util.redis.PlayerPrefix;
import com.foolox.game.common.util.redis.RedisService;
import com.foolox.game.common.util.redis.SystemPrefix;
import com.foolox.game.constants.PlayerGameStatus;
import com.foolox.game.constants.PlayerStatus;
import com.foolox.game.core.FooloxDataContext;
import com.foolox.game.common.repo.domain.ClientSession;
import com.foolox.game.core.engin.game.event.Board;
import com.foolox.game.core.engin.game.event.GameStatus;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.tio.core.Aio;
import org.tio.core.ChannelContext;
import org.tio.http.common.HttpRequest;
import org.tio.http.common.HttpResponse;
import org.tio.websocket.common.WsRequest;
import org.tio.websocket.server.handler.IWsMsgHandler;

import java.io.IOException;
import java.nio.ByteBuffer;

/**
 * comment:
 *
 * @author: lipengfei
 * @date: 06/05/2019
 */
@Slf4j
public class GameEventHandler implements IWsMsgHandler {

    protected GameServer server;

    public GameServer getServer() {
        return server;
    }

    public void setServer(GameServer server) {
        this.server = server;
    }

    private RedisService redisService = FooloxDataContext.getApplicationContext().getBean(RedisService.class);

    /**
     * 握手时走这个方法，业务可以在这里获取cookie，request参数等
     */
    @Override
    public HttpResponse handshake(HttpRequest httpRequest, HttpResponse httpResponse, ChannelContext channelContext) throws Exception {
        String userid = httpRequest.getParam("userid");
        if (!StringUtils.isBlank(userid)) {
            channelContext.setAttribute(userid, userid);
            //绑定用户ID
            Aio.bindUser(channelContext, userid);
            //STAY 鉴权
        }
        return httpResponse;
    }

    /**
     * 字节消息（binaryType = arraybuffer）过来后会走这个方法
     */
    @Override
    public Object onBytes(WsRequest wsRequest, byte[] bytes, ChannelContext channelContext) throws Exception {
        channelContext.getClientNode().getIp();
        ByteBuffer buffer = ByteBuffer.allocate(1);
        return buffer;
    }

    /**
     * 当客户端发close flag时，会走这个方法
     */
    @Override
    public Object onClose(WsRequest wsRequest, byte[] bytes, ChannelContext channelContext) throws Exception {
        Aio.remove(channelContext, "receive close flag");


//        BeiMiClient beiMiClient = NettyClients.getInstance().getClient(channelContext.getUserid()) ;
//        if(beiMiClient!=null){
//            /**
//             * 玩家离线
//             */
//            PlayUserClient playUserClient = (PlayUserClient) CacheHelper.getApiUserCacheBean().getCacheObject(beiMiClient.getUserid(), beiMiClient.getOrgi()) ;
//            if(playUserClient!=null){
//                if(BMDataContext.GameStatusEnum.PLAYING.toString().equals(playUserClient.getPlayerGameStatus())){
//                    GameUtils.updatePlayerClientStatus(beiMiClient.getUserid(), beiMiClient.getOrgi(), BMDataContext.PlayerTypeEnum.OFFLINE.toString());
//                }else{
//                    CacheHelper.getApiUserCacheBean().delete(beiMiClient.getUserid(), beiMiClient.getOrgi()) ;
//                    if(CacheHelper.getGamePlayerCacheBean().getClientSessions(beiMiClient.getUserid(), beiMiClient.getOrgi())!=null){
//                        CacheHelper.getGamePlayerCacheBean().delete(beiMiClient.getUserid(), beiMiClient.getOrgi()) ;
//                    }
//                    CacheHelper.getRoomMappingCacheBean().delete(beiMiClient.getUserid(), beiMiClient.getOrgi()) ;
//                    /**
//                     * 玩家退出游戏，需要发送事件给所有玩家，如果房主退出，则房间解散
//                     */
//                }
//                /**
//                 * 退出房间，房卡模式下如果房间还有剩余局数 ， 则不做任何操作，如果无剩余或未开始扣卡，则删除房间
//                 */
//            }
//        }
        return null;
    }

    /**
     * 字符消息（binaryType = blob）过来后会走这个方法
     */
    @Override
    public Object onText(WsRequest wsRequest, String text, ChannelContext channelContext) throws Exception {
        if (text != null) {
            FooloxClient fooloxClient = JSON.parseObject(text, FooloxClient.class);
            if (!StringUtils.isBlank(fooloxClient.getCommand())) {
                fooloxClient.setServer(this.server);
                switch (fooloxClient.getCommand()) {
                    case "joinroom":
                        this.onJoinRoom(fooloxClient);
                        break;
                    case "playerGameStatus":
                        this.onGameStatus(fooloxClient);
                        break;
                    case "docatch":this.onCatch(fooloxClient); break;
//                    case "giveup":this.onGiveup(fooloxClient); break;
//                    case "cardtips":this.onCardTips(fooloxClient); break;
//                    case "doplaycards":this.onPlayCards(fooloxClient); break;
//                    case "nocards":this.onNoCards(fooloxClient); break;
//                    case "selectcolor":this.onSelectColor(fooloxClient); break;
//                    case "selectaction":this.onActionEvent(fooloxClient); break;
//                    case "restart":this.onRestart(fooloxClient); break;
//                    case "start":this.onStart(fooloxClient); break;
//                    case "recovery":this.onRecovery(fooloxClient); break;
//                    case "leave":this.onLeave(fooloxClient); break;
//                    case "command":this.onCommand(fooloxClient); break;
//                    case "searchroom":this.onSearchRoom(fooloxClient); break;
//                    case "message":this.onMessage(fooloxClient); break;

                }
            }
        }
        return null;
    }


    /**
     * 加入房间
     *
     * @param fooloxClient
     */
    public void onJoinRoom(FooloxClient fooloxClient) {
        String token = fooloxClient.getToken();
        String userId = fooloxClient.getUserid();
        if (!StringUtils.isBlank(token) && !StringUtils.isBlank(userId)) {
            /**
             * Token不为空，并且，验证Token有效，验证完毕即开始进行游戏撮合，房卡类型的
             * 1、大厅房间处理
             *    a、从房间队列里获取最近一条房间信息
             *    b、将token对应玩家加入到房间
             *    c、如果房间凑齐了玩家，则将房间从等待撮合队列中移除，放置到游戏中的房间信息，如果未凑齐玩家，继续扔到队列
             *    d、通知房间的所有人，有新玩家加入
             *    e、超时处理，增加AI进入房价
             *    f、事件驱动
             *    g、定时器处理
             * 2、房卡房间处理
             * 	  a、创建房间
             * 	  b、加入到等待中队列
             */
            String redisToken = redisService.get(PlayerPrefix.TOKEN, userId);
            //鉴权通过
            if (redisToken != null && token.equals(redisToken)) {
                //心跳开始时间
                fooloxClient.setTime(System.currentTimeMillis());
                //保存客户端，以备定时任务给客户端发消息
                FooloxClientContext.getFooloxClientCache().putClient(fooloxClient.getUserid(), fooloxClient);

                ClientSession clientSession = FooloxUtils.getClientSessionById(userId);
                if (null == clientSession) {
                    log.error("登录用户已保存 ClientSession 到redis，clientSession 不应为空！");
                }
                //更新当前玩家状态，在线|离线
                clientSession.setOnline(true);
                //更新玩家类型为普通玩家
                clientSession.setPlayerStatus(PlayerStatus.NORMAL);
                //刷新玩家信息到缓存
                FooloxUtils.setClientSessionById(userId,clientSession);

                //处理玩家不在游戏中
                GameUtils.syncNotPlaying(clientSession);
                //异步保存ClientSession到数据库
                FooloxUtils.published(clientSession, FooloxDataContext.getApplicationContext().getBean(ClientSessionRepository.class));
                //请求游戏
                FooloxDataContext.getGameEngine().gameRequest(clientSession, fooloxClient);
            }
        }
    }

    /**
     * 查看游戏状态
     * @param fooloxClient
     */
    public void onGameStatus(FooloxClient fooloxClient) {
        String token = fooloxClient.getToken();
        String userId = fooloxClient.getUserid();
        GameStatus gameStatus = new GameStatus();
        gameStatus.setGamestatus(PlayerGameStatus.NOTREADY.toString());
        if(!StringUtils.isBlank(token) && !StringUtils.isBlank(userId)) {
            String redisToken = redisService.get(PlayerPrefix.TOKEN, userId);
            //鉴权
            if (redisToken != null && token.equals(redisToken)) {
                ClientSession clientSession = FooloxUtils.getClientSessionById(userId);
                if (null!=clientSession) {
                    gameStatus.setGamestatus(PlayerGameStatus.READY.toString());
                    String roomid = redisService.get(GamePrefix.ROOM_USERID_GAMEROOMID, clientSession.getUserId());
                    if (!StringUtils.isBlank(roomid) && FooloxUtils.getBoardByRoomId(roomid, Board.class) != null) {
                        gameStatus.setUserid(clientSession.getId());
                        gameStatus.setOrgi(clientSession.getOrgi());

                        GameRoom gameRoom = redisService.get(GamePrefix.ROOM_ROOMID_GAMEROOM, roomid, GameRoom.class);
                        GamePlayway gamePlayway = redisService.get(SystemPrefix.CONFIG_ID_PLAYWAY, gameRoom.getPlaywayId(), GamePlayway.class);
                        gameStatus.setGametype(gamePlayway.getCode());
                        gameStatus.setPlayway(gamePlayway.getId());
                        gameStatus.setGamestatus(PlayerGameStatus.PLAYING.toString());
                        if (gameRoom.isCardroom()) {
                            gameStatus.setCardroom(true);
                        }
                    }
                }

            } else {
                gameStatus.setGamestatus(PlayerGameStatus.TIMEOUT.toString());
            }
        }
        fooloxClient.sendEvent(FooloxDataContext.FOOLOX_GAMESTATUS_EVENT, gameStatus);
    }

    /**
     * 叫庄
     * @param fooloxClient
     */
    public void onCatch(FooloxClient fooloxClient) {
        String token = fooloxClient.getToken();
        String userId = fooloxClient.getUserid();
        if(!StringUtils.isBlank(token) && !StringUtils.isBlank(userId)) {
            String redisToken = redisService.get(PlayerPrefix.TOKEN, userId);
            //鉴权
            if (redisToken != null && token.equals(redisToken)) {
                ClientSession clientSession = FooloxUtils.getClientSessionById(userId);
                String roomid = redisService.get(GamePrefix.ROOM_USERID_GAMEROOMID, clientSession.getUserId());
                FooloxDataContext.getGameEngine().actionRequest(roomid, clientSession, clientSession.getOrgi(), true);
            }
        }
    }
}
