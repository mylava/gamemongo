package com.foolox.game.core.server.handler;

import com.foolox.game.common.repo.domain.ClientSession;
import com.foolox.game.common.repo.domain.GamePlayway;
import com.foolox.game.common.repo.domain.GameRoom;
import com.foolox.game.common.result.CodeMessage;
import com.foolox.game.common.util.FooloxUtils;
import com.foolox.game.constants.PlayerGameStatus;
import com.foolox.game.core.FooloxDataContext;
import com.foolox.game.core.engin.game.event.Board;
import com.foolox.game.core.engin.game.event.CommonError;
import com.foolox.game.core.engin.game.event.GameStatus;
import com.foolox.game.core.server.FooloxClient;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

/**
 * comment:
 *
 * @author: lipengfei
 * @date: 25/06/2019
 */
@Slf4j
@Command
public class GamestatusCommand implements EventCommand{

    @Override
    public void execute(FooloxClient fooloxClient) {
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
