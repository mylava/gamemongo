package com.foolox.game.core.server.handler;

import com.foolox.game.core.server.FooloxClient;

/**
 * comment:
 *
 * @author: lipengfei
 * @date: 26/06/2019
 */
public interface EventCommand {
    void execute(FooloxClient fooloxClient);
}
