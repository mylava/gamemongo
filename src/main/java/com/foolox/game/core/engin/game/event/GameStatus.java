package com.foolox.game.core.engin.game.event;

import com.foolox.game.common.result.CodeMessage;
import com.foolox.game.constants.Command;
import com.foolox.game.core.engin.game.Message;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * comment:
 *
 * @author: lipengfei
 * @date: 30/05/2019
 */
@Data
@NoArgsConstructor
public class GameStatus implements Message {
    //暂时无用
    private Command command;
    private String gamestatus;
    private String userid;
    private String gametype;
    private String playway;
    private boolean cardroom;
    private String event;
    private CodeMessage codeMessage;
}
