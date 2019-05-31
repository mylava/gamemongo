package com.foolox.game.core.server;

import org.tio.server.ServerGroupContext;
import org.tio.websocket.server.WsServerStarter;

import java.io.IOException;

/**
 * comment:
 *
 * @author: lipengfei
 * @date: 06/05/2019
 */
public class GameServer {
    //给所有客户端推送消息时使用
    private ServerGroupContext serverGroupContext;
    private WsServerStarter wsServerStarter;

    public GameServer(int port, GameEventHandler wsMsgHandler) throws IOException {
        wsServerStarter = new WsServerStarter(port, wsMsgHandler);
        serverGroupContext = wsServerStarter.getServerGroupContext();
    }

    public void start() throws IOException {
        wsServerStarter.start();
    }

    public ServerGroupContext getServerGroupContext() {
        return serverGroupContext;
    }

    public WsServerStarter getWsServerStarter() {
        return wsServerStarter;
    }
}
