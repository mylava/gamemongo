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
    private Map<String, FooloxClient> clientMap = new HashMap<String,FooloxClient>();
    public FooloxClient getClient(String key){
        return  clientMap.get(key);
    }

    public void putClient(String userid , FooloxClient client){
        clientMap.put(userid, client) ;
    }

    public void removeClient(String id){
        FooloxClient FooloxClient = this.getClient(id) ;
        clientMap.remove(id) ;
        if(FooloxClient!=null){
            clientMap.remove(FooloxClient.getUserid()) ;
        }
    }

    public void joinRoom(String userid, String roomid) {
        FooloxClient FooloxClient = this.getClient(userid) ;
        if(FooloxClient!=null){
//			FooloxClient.getClient().joinRoom(roomid);
        }
    }

    public void sendGameEventMessage(String userid, String event, Message data) {
        FooloxClient FooloxClient = this.getClient(userid) ;
        if(FooloxClient!=null){
            FooloxClient.sendEvent(event, data);
        }
    }
}
