package com.foolox.game.core.logic;

import com.foolox.game.common.repo.domain.ClientSession;
import com.foolox.game.common.repo.domain.GamePlayway;
import com.foolox.game.common.repo.domain.GameRoom;
import com.foolox.game.core.FooloxGameProcessor;
import com.foolox.game.core.engin.game.event.Board;

import java.util.List;

/**
 * comment:
 *
 * @author: lipengfei
 * @date: 30/05/2019
 */
public class MajiangGameProcessor implements FooloxGameProcessor {
    @Override
    public Board process(List<ClientSession> clientSessions, GameRoom gameRoom, GamePlayway playway, String banker, int cardsnum) {
        return null;
    }
}
