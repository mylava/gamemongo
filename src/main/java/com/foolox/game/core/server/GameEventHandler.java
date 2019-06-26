package com.foolox.game.core.server;

import com.alibaba.fastjson.JSON;
import com.foolox.game.common.repo.dao.ClientSessionRepository;
import com.foolox.game.common.repo.domain.ClientSession;
import com.foolox.game.common.repo.domain.GamePlayway;
import com.foolox.game.common.repo.domain.GameRoom;
import com.foolox.game.common.result.CodeMessage;
import com.foolox.game.common.util.FooloxUtils;
import com.foolox.game.common.util.GameUtils;
import com.foolox.game.common.util.client.FooloxClientContext;
import com.foolox.game.constants.ClientCommand;
import com.foolox.game.constants.PlayerGameStatus;
import com.foolox.game.constants.PlayerType;
import com.foolox.game.constants.SearchRoomResultType;
import com.foolox.game.core.FooloxDataContext;
import com.foolox.game.core.engin.game.event.Board;
import com.foolox.game.core.engin.game.event.CommonError;
import com.foolox.game.core.engin.game.event.GameStatus;
import com.foolox.game.core.engin.game.event.SearchRoomResult;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.tio.core.Aio;
import org.tio.core.ChannelContext;
import org.tio.http.common.HttpRequest;
import org.tio.http.common.HttpResponse;
import org.tio.websocket.common.WsRequest;
import org.tio.websocket.server.handler.IWsMsgHandler;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

/**
 * comment:
 *
 * @author: lipengfei
 * @date: 06/05/2019
 */
@Slf4j
public class GameEventHandler implements IWsMsgHandler {

    protected GameServer server;

    private List<CommandHandler> handlerList = new ArrayList<>();

    public GameServer getServer() {
        return server;
    }

    public void setServer(GameServer server) {
        this.server = server;
    }

    public void addHandler(CommandHandler commandHandler) {
        handlerList.add(commandHandler);
    }
    /**
     * 握手时走这个方法，业务可以在这里获取cookie，request参数等
     */
    @Override
    public HttpResponse handshake(HttpRequest httpRequest, HttpResponse httpResponse, ChannelContext channelContext) throws Exception {
        String userid = httpRequest.getParam("userId");
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
        return null;
    }

    /**
     * 字符消息（binaryType = blob）过来后会走这个方法
     */
    @Override
    public Object onText(WsRequest wsRequest, String text, ChannelContext channelContext) throws Exception {
        if (text != null) {
            //在客户端创建，有客户端传入Json进行反序列化生成
            FooloxClient fooloxClient = JSON.parseObject(text, FooloxClient.class);
            if (!StringUtils.isBlank(fooloxClient.getCommand())) {
                fooloxClient.setServer(this.server);
                String command = fooloxClient.getCommand();
                CommandHandler handler = null;
                for (CommandHandler h : handlerList) {
                    if (h.getCommand().equals(command)) {
                        handler = h;
                        break;
                    }
                }
                if (null!=handler) {
                    handler.getMethod().invoke(handler.getHandler(), fooloxClient);
                } else {
                    log.info("command has no handler, command is {}", command);
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
        //参数校验
        if (!checkAuthParams(fooloxClient)) {
            return;
        }

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
        //鉴权
        if (!checkAuth(fooloxClient)) {
            return;
        }
        //具体业务参数校验
        String playwayId = fooloxClient.getPlaywayId();
        log.info("playwayId={}", playwayId);
        if (StringUtils.isBlank(playwayId)) {
            CommonError commonError = new CommonError();
            CodeMessage codeMessage = CodeMessage.PARAMS_EMPTY_ERROR.fillArgs("playwayId");
            commonError.setCode(codeMessage.getCode());
            commonError.setInfo(codeMessage.getMessage());
            fooloxClient.sendEvent(FooloxDataContext.FOOLOX_GAMESTATUS_EVENT, commonError);
            return;
        }

        String userId = fooloxClient.getUserId();
        ClientSession clientSession = FooloxUtils.getClientSessionById(userId);
        //心跳开始时间
        fooloxClient.setTime(System.currentTimeMillis());
        //保存客户端，以备与客户端通信
        FooloxClientContext.getFooloxClientCache().putClient(fooloxClient.getUserId(), fooloxClient);
        //更新当前玩家状态，在线|离线
        clientSession.setOnline(true);
        //更新玩家类型为普通玩家
        clientSession.setPlayerType(PlayerType.NORMAL);
        //刷新玩家信息到缓存
        FooloxUtils.setClientSessionById(userId, clientSession);

        //之前占用的有房间，且游戏已经打完了，则清除
        GameUtils.syncNotPlaying(clientSession);
        //异步保存ClientSession到数据库 (登录日志)
        FooloxUtils.published(clientSession, FooloxDataContext.getApplicationContext().getBean(ClientSessionRepository.class));
        //请求游戏
        FooloxDataContext.getGameEngine().gameRequest(clientSession, fooloxClient);
    }

    /**
     * 查看游戏状态
     *
     * @param fooloxClient
     */
    public void onGameStatus(FooloxClient fooloxClient) {
        //参数校验
        if (!checkAuthParams(fooloxClient)) {
            return;
        }
        //鉴权
        if (!checkAuth(fooloxClient)) {
            return;
        }

        String userId = fooloxClient.getUserId();
        ClientSession clientSession = FooloxUtils.getClientSessionById(userId);
        log.info("user {} is ready ", userId);
        GameStatus gameStatus = new GameStatus();
        //鉴权通过，更新玩家状态为就绪（可以游戏）
        gameStatus.setGamestatus(PlayerGameStatus.READY.toString());
        //查看玩家是否在房间内
        String roomId = FooloxUtils.getRoomIdByUserId(userId);
        //Room 和 Board 都不为空，表示已经在游戏中（断线重连情况）
        if (!StringUtils.isBlank(roomId) && FooloxUtils.getBoardByRoomId(roomId, Board.class) != null) {
            gameStatus.setUserid(clientSession.getUserId());
            //读取房间信息
            GameRoom gameRoom = FooloxUtils.getRoomById(roomId);
            //读取玩法信息
            GamePlayway gamePlayway = FooloxUtils.getGamePlaywayById(gameRoom.getPlaywayId());
            gameStatus.setGametype(gamePlayway.getModelCode());
            gameStatus.setPlayway(gamePlayway.getId());
            //更新玩家状态为游戏中
            log.info("user {} is playing, gameRoom = {}", userId, gameRoom);
            gameStatus.setGamestatus(PlayerGameStatus.PLAYING.toString());
            //房卡游戏
            if (gameRoom.isCardroom()) {
                gameStatus.setCardroom(true);
            }
        }
        //发送gameStatus到客户端
        fooloxClient.sendEvent(FooloxDataContext.FOOLOX_GAMESTATUS_EVENT, gameStatus);
    }

    /**
     * 叫庄
     *
     * @param fooloxClient
     */
    public void onCatch(FooloxClient fooloxClient) {
        String token = fooloxClient.getToken();
        String userId = fooloxClient.getUserId();
        if (!StringUtils.isBlank(token) && !StringUtils.isBlank(userId)) {
            String redisToken = FooloxUtils.getTokenByUserId(userId);
            //鉴权
            if (redisToken != null && token.equals(redisToken)) {
                String roomid = FooloxUtils.getRoomIdByUserId(userId);
                FooloxDataContext.getGameEngine().actionRequest(roomid, userId, true);
            }
        }
    }

    /**
     * 不叫
     *
     * @param fooloxClient
     */
    public void onGiveup(FooloxClient fooloxClient) {
        String token = fooloxClient.getToken();
        String userId = fooloxClient.getUserId();
        if (!StringUtils.isBlank(token) && !StringUtils.isBlank(userId)) {
            String redisToken = FooloxUtils.getTokenByUserId(userId);
            //鉴权
            if (redisToken != null && token.equals(redisToken)) {
                String roomid = FooloxUtils.getRoomIdByUserId(userId);
                FooloxDataContext.getGameEngine().actionRequest(roomid, userId, false);
            }
        }
    }

    /**
     * 自动出牌（默认出最小的牌）
     *
     * @param fooloxClient
     */
    public void onCardTips(FooloxClient fooloxClient) {
        String token = fooloxClient.getToken();
        String userId = fooloxClient.getUserId();
        if (!StringUtils.isBlank(token) && !StringUtils.isBlank(userId)) {
            String redisToken = FooloxUtils.getTokenByUserId(userId);
            //鉴权
            if (redisToken != null && token.equals(redisToken)) {
                ClientSession clientSession = FooloxUtils.getClientSessionById(userId);
                String roomid = FooloxUtils.getRoomIdByUserId(userId);
                FooloxDataContext.getGameEngine().cardTips(roomid, clientSession, fooloxClient.getData());
            }
        }
    }

    /**
     * 出牌
     *
     * @param fooloxClient
     */
    public void onPlayCards(FooloxClient fooloxClient) {
        String token = fooloxClient.getToken();
        String userId = fooloxClient.getUserId();
        if (!StringUtils.isBlank(token) && !StringUtils.isBlank(userId)) {
            String redisToken = FooloxUtils.getTokenByUserId(userId);
            //鉴权
            if (redisToken != null && token.equals(redisToken)) {
                String roomid = FooloxUtils.getRoomIdByUserId(userId);
                String[] cards = fooloxClient.getData().split(",");

                byte[] playCards = new byte[cards.length];
                for (int i = 0; i < cards.length; i++) {
                    playCards[i] = Byte.parseByte(cards[i]);
                }
                FooloxDataContext.getGameEngine().takeCardsRequest(roomid, userId, false, playCards);
            }
        }
    }

    /**
     * 牌出完
     *
     * @param fooloxClient
     */
    public void onNoCards(FooloxClient fooloxClient) {
        String token = fooloxClient.getToken();
        String userId = fooloxClient.getUserId();
        if (!StringUtils.isBlank(token) && !StringUtils.isBlank(userId)) {
            String redisToken = FooloxUtils.getTokenByUserId(userId);
            //鉴权
            if (redisToken != null && token.equals(redisToken)) {
                String roomid = FooloxUtils.getRoomIdByUserId(userId);
                FooloxDataContext.getGameEngine().takeCardsRequest(roomid, userId, false, null);
            }
        }
    }

    /**
     * 定缺
     *
     * @param fooloxClient
     */
    public void onSelectColor(FooloxClient fooloxClient) {
        String token = fooloxClient.getToken();
        String userId = fooloxClient.getUserId();
        if (!StringUtils.isBlank(token) && !StringUtils.isBlank(userId)) {
            String redisToken = FooloxUtils.getTokenByUserId(userId);
            //鉴权
            if (redisToken != null && token.equals(redisToken)) {
                String roomid = FooloxUtils.getRoomIdByUserId(userId);
                FooloxDataContext.getGameEngine().selectColorRequest(roomid, userId, fooloxClient.getData());
            }
        }
    }

    /**
     * 碰杠胡过动作处理
     *
     * @param fooloxClient
     */
    public void onActionEvent(FooloxClient fooloxClient) {
        String token = fooloxClient.getToken();
        String userId = fooloxClient.getUserId();
        if (!StringUtils.isBlank(token) && !StringUtils.isBlank(userId)) {
            String redisToken = FooloxUtils.getTokenByUserId(userId);
            //鉴权
            if (redisToken != null && token.equals(redisToken)) {
                String roomid = FooloxUtils.getRoomIdByUserId(userId);
                FooloxDataContext.getGameEngine().actionEventRequest(roomid, userId, fooloxClient.getData());
            }
        }
    }

    /**
     * 一局打完，重开一局
     *
     * @param fooloxClient
     */
    public void onRestart(FooloxClient fooloxClient) {
        String token = fooloxClient.getToken();
        String userId = fooloxClient.getUserId();
        if (!StringUtils.isBlank(token) && !StringUtils.isBlank(userId)) {
            String redisToken = FooloxUtils.getTokenByUserId(userId);
            //鉴权
            if (redisToken != null && token.equals(redisToken)) {
                ClientSession clientSession = FooloxUtils.getClientSessionById(userId);
                String roomid = FooloxUtils.getRoomIdByUserId(userId);
                FooloxDataContext.getGameEngine().restartRequest(roomid, clientSession, fooloxClient, "true".equals(fooloxClient.getData()));
            }
        }
    }

    /**
     * 游戏开始
     *
     * @param fooloxClient
     */
    public void onStart(FooloxClient fooloxClient) {
        String token = fooloxClient.getToken();
        String userId = fooloxClient.getUserId();
        if (!StringUtils.isBlank(token) && !StringUtils.isBlank(userId)) {
            String redisToken = FooloxUtils.getTokenByUserId(userId);
            //鉴权
            if (redisToken != null && token.equals(redisToken)) {
                ClientSession clientSession = FooloxUtils.getClientSessionById(userId);
                if (clientSession != null) {
                    FooloxDataContext.getGameEngine().startGameRequest(clientSession.getRoomId(), clientSession, "true".equals(fooloxClient.getData()));
                }
            }
        }
    }

    /**
     * 断线重连
     *
     * @param fooloxClient
     */
    public void onRecovery(FooloxClient fooloxClient) {
        String token = fooloxClient.getToken();
        String userId = fooloxClient.getUserId();
        if (!StringUtils.isBlank(token) && !StringUtils.isBlank(userId)) {
            String redisToken = FooloxUtils.getTokenByUserId(userId);
            //鉴权
            if (redisToken != null && token.equals(redisToken)) {
                ClientSession clientSession = FooloxUtils.getClientSessionById(userId);
                FooloxDataContext.getGameEngine().gameRequest(clientSession.getUserId(), fooloxClient.getPlaywayId(), fooloxClient, clientSession);
            }
        }
    }

    /**
     * 离开房间
     *
     * @param fooloxClient
     */
    public void onLeave(FooloxClient fooloxClient) {
        String token = fooloxClient.getToken();
        String userId = fooloxClient.getUserId();
        if (!StringUtils.isBlank(token) && !StringUtils.isBlank(userId)) {
            String redisToken = FooloxUtils.getTokenByUserId(userId);
            //鉴权
            if (redisToken != null && token.equals(redisToken)) {
                GameUtils.updatePlayerClientStatus(fooloxClient.getUserId(), PlayerType.LEAVE);
            }
        }
    }

    /**
     * 补充命令
     *
     * @param fooloxClient
     */
    public void onCommand(FooloxClient fooloxClient) {
        ClientCommand clientCommand = JSON.parseObject(fooloxClient.getData(), ClientCommand.class);
        if (clientCommand != null && !StringUtils.isBlank(clientCommand.getToken())) {
            String token = fooloxClient.getToken();
            String userId = fooloxClient.getUserId();
            if (!StringUtils.isBlank(token) && !StringUtils.isBlank(userId)) {
                String redisToken = FooloxUtils.getTokenByUserId(userId);
                //鉴权
                if (redisToken != null && token.equals(redisToken)) {
                    ClientSession clientSession = FooloxUtils.getClientSessionById(userId);
                    switch (clientCommand.getCommand()) {
                        //破产补助
                        case "subsidy":
                            GameUtils.subsidy(fooloxClient, clientSession);
                            break;
                        default:
                            break;
                    }
                }
            }
        }
    }

    /**
     * 查找房间
     *
     * @param fooloxClient
     */
    public void onSearchRoom(FooloxClient fooloxClient) {
        String token = fooloxClient.getToken();
        String userId = fooloxClient.getUserId();
        String roomId = fooloxClient.getData();
        GamePlayway gamePlayway = null;
        SearchRoomResult searchRoomResult = null;
        boolean joinRoom = false;
        if (!StringUtils.isBlank(token) && !StringUtils.isBlank(userId)) {
            String redisToken = FooloxUtils.getTokenByUserId(userId);
            //鉴权
            if (redisToken != null && token.equals(redisToken)) {
                ClientSession clientSession = FooloxUtils.getClientSessionById(userId);
                GameRoom gameRoom = FooloxUtils.getRoomById(roomId);
                if (gameRoom != null) {
                    /**
                     * 将玩家加入到房间中来，加入的时候需要处理当前的房间已满员或未满员，如果满员，需要检查是否允许围观
                     */
                    gamePlayway = FooloxUtils.getGamePlaywayById(gameRoom.getPlaywayId());
                    List<ClientSession> playerList = FooloxUtils.getRoomClientSessionList(gameRoom.getId());
                    if (playerList.size() < gamePlayway.getMaxPlayerNum()) {
                        FooloxDataContext.getGameEngine().joinRoom(gameRoom, clientSession, playerList);
                        joinRoom = true;
                    }
                }
                /**
                 * 获取的玩法，将玩法数据发送给当前请求的玩家
                 */
                if (gamePlayway != null) {
                    //通知客户端
                    if (joinRoom) {        //加入成功 ， 是否需要输入加入密码？
                        searchRoomResult = new SearchRoomResult(gamePlayway.getId(), gamePlayway.getModelCode(), SearchRoomResultType.OK.toString());
                    } else {                        //加入失败
                        searchRoomResult = new SearchRoomResult(SearchRoomResultType.FULL.toString());
                    }
                } else { //房间不存在
                    searchRoomResult = new SearchRoomResult(SearchRoomResultType.NOTEXIST.toString());
                }
                fooloxClient.sendEvent(FooloxDataContext.FOOLOX_SEARCHROOM_EVENT, searchRoomResult);
            }
        }
    }

    /**
     * 聊天
     *
     * @param fooloxClient
     */
    public void onMessage(FooloxClient fooloxClient) {
        String token = fooloxClient.getToken();
        String userId = fooloxClient.getUserId();
        if (!StringUtils.isBlank(token) && !StringUtils.isBlank(userId)) {
            String redisToken = FooloxUtils.getTokenByUserId(userId);
            //鉴权
            if (redisToken != null && token.equals(redisToken)) {
                String roomId = FooloxUtils.getRoomIdByUserId(userId);
                GameRoom gameRoom = FooloxUtils.getRoomById(roomId);
//                ActionTaskUtils.sendEvent(Command.MESSAGE, new ChatMessage(),gameRoom);
            }
        }
    }

    /**
     * 检查鉴权必填参数
     *
     * @return
     */
    private boolean checkAuthParams(FooloxClient fooloxClient) {
        String token = fooloxClient.getToken();
        String userId = fooloxClient.getUserId();
        log.info("token={}, userId={}", token, userId);
        if (StringUtils.isBlank(token) || StringUtils.isBlank(userId)) {
            CommonError commonError = new CommonError();
            CodeMessage codeMessage = CodeMessage.PARAMS_EMPTY_ERROR.fillArgs("token", "userId");
            commonError.setCode(codeMessage.getCode());
            commonError.setInfo(codeMessage.getMessage());
            fooloxClient.sendEvent(FooloxDataContext.FOOLOX_GAMESTATUS_EVENT, commonError);
            return false;
        }
        return true;
    }

    /**
     * 鉴权
     *
     * @param fooloxClient
     * @return
     */
    private boolean checkAuth(FooloxClient fooloxClient) {
        String token = fooloxClient.getToken();
        String userId = fooloxClient.getUserId();
        String redisToken = FooloxUtils.getTokenByUserId(userId);
        log.info("redisToken={}", redisToken);
        if (null == redisToken || !token.equals(redisToken)) {
            CommonError commonError = new CommonError();
            CodeMessage codeMessage = CodeMessage.ILLEGAL_TOKEN_ERROR.fillArgs("token", "userId");
            commonError.setCode(codeMessage.getCode());
            commonError.setInfo(codeMessage.getMessage());
            fooloxClient.sendEvent(FooloxDataContext.FOOLOX_GAMESTATUS_EVENT, commonError);
            return false;
        }

        ClientSession clientSession = FooloxUtils.getClientSessionById(userId);
        log.info("clientSession={}", clientSession);
        if (null == clientSession) {
            CommonError commonError = new CommonError();
            CodeMessage codeMessage = CodeMessage.ILLEGAL_TOKEN_ERROR.fillArgs("clientSession");
            commonError.setCode(codeMessage.getCode());
            commonError.setInfo(codeMessage.getMessage());
            fooloxClient.sendEvent(FooloxDataContext.FOOLOX_GAMESTATUS_EVENT, commonError);
            return false;
        }
        return true;
    }
}