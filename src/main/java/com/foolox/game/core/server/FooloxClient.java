package com.foolox.game.core.server;

import com.foolox.game.constants.RoomType;
import com.foolox.game.core.engin.game.Message;
import lombok.Data;
import org.tio.core.Aio;
import org.tio.utils.json.Json;
import org.tio.websocket.common.Opcode;
import org.tio.websocket.common.WsResponse;

import java.io.IOException;
import java.util.Map;

/**
 * comment: 与客户端交互使用的对象
 *
 * @author: lipengfei
 * @date: 07/05/2019
 */
@Data
public class FooloxClient {
    private GameServer server;
    private String userId;
    //用于前端传命令
    private String command;
    //用于鉴权
    private String token;
    //建立心跳的时间
    private long time;
    //玩法
    private String playwayId;
    //房间ID
    private String room;
    //游戏模式：大厅、房卡、俱乐部
    private RoomType roomType;
    //额外参数
    private Map<String,  String> extparams ;

    private String data;

    public void sendEvent(String event, Message msg) {
        try {
            msg.setEvent(event);
            Aio.sendToUser(this.server.getServerGroupContext(), this.userId, convertToTextResponse(msg));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * @param body
     * @return
     * @throws IOException
     */
    private WsResponse convertToTextResponse(Message body) throws IOException {
        WsResponse response = new WsResponse();
        if (body != null) {
            String json = Json.toJson(body);
            response.setBody(json.getBytes("UTF-8"));
            response.setWsBodyText(json);
            response.setWsBodyLength(response.getWsBodyText().length());
            //返回text类型消息（如果这里设置成 BINARY,那么客户端就需要进行解析了）
            response.setWsOpcode(Opcode.TEXT);
        }
        return response;
    }
}
