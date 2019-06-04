package com.foolox.game.core;

import com.foolox.game.common.repo.domain.ClientSession;
import com.foolox.game.common.repo.domain.GamePlayway;
import com.foolox.game.common.repo.domain.GameRoom;
import com.foolox.game.core.engin.game.event.Board;

import java.util.List;

/**
 * comment: 棋牌游戏接口
 *
 * @author: lipengfei
 * @date: 30/05/2019
 */
public interface FooloxGame {
    /**
     * 创建一局新游戏
     *
     * @return
     */
    Board process(List<ClientSession> clientSessions, GameRoom gameRoom, GamePlayway playway, String banker, int cardsnum);

}
