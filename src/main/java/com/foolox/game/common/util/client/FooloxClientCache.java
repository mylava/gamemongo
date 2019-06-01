package com.foolox.game.common.util.client;

import com.foolox.game.core.engin.game.Message;
import com.foolox.game.core.server.FooloxClient;

import java.util.HashMap;
import java.util.Map;

/**
 * comment: 保存 FooloxClient 以备定时任务发消息使用
 *
 * @author: lipengfei
 * @date: 19/05/2019
 */
public class FooloxClientCache {
    private Map<String, FooloxClient> clientMap = new HashMap<String, FooloxClient>();

    public FooloxClient getClient(String key) {
        return clientMap.get(key);
    }

    /**
     * 保存UserId 与 FooloxClient 映射关系
     * @param userId
     * @param client
     */
    public void putClient(String userId, FooloxClient client) {
        clientMap.put(userId, client);
    }

    public void removeClient(String userId) {
        FooloxClient client = this.getClient(userId);
        clientMap.remove(userId);
        if (client != null) {
            clientMap.remove(client.getUserId());
        }
    }

    public void joinRoom(String userId, String roomid) {
        FooloxClient client = this.getClient(userId);
        if (client != null) {
//			FooloxClient.getClient().joinRoom(roomid);
        }
    }

    public void sendGameEventMessage(String userId, String event, Message data) {
        FooloxClient client = this.getClient(userId);
        if (client != null) {
            client.sendEvent(event, data);
        }
    }
}
