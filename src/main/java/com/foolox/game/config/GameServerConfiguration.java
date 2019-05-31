package com.foolox.game.config;

import com.foolox.game.core.server.GameEventHandler;
import com.foolox.game.core.server.GameServer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;

/**
 * comment:
 *
 * @author: lipengfei
 * @date: 06/05/2019
 */
@Configuration
public class GameServerConfiguration {
    @Value("${foolox.server.port}")
    private Integer port;
    private GameEventHandler handler = new GameEventHandler();

    @Bean
    public GameServer socketIOServer() throws NoSuchAlgorithmException, IOException {
        GameServer server = new GameServer(port , handler) ;
        handler.setServer(server);
        return server;
    }
}
