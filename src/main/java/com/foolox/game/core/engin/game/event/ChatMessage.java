package com.foolox.game.core.engin.game.event;

import com.foolox.game.constants.Command;
import com.foolox.game.core.engin.game.Message;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * comment:
 *
 * @author: lipengfei
 * @date: 31/05/2019
 */
@Data
@NoArgsConstructor
public class ChatMessage implements Message {
    private Command command;
    private String event;
    //聊天对象
    private List<String> targetUserId;
    //聊天内容
    private String chat;

    public ChatMessage(List<String> targetUserId, String chat) {
        this.targetUserId = targetUserId;
        this.chat = chat;
    }
}
