package com.foolox.game.core.engin.game.event;

import com.foolox.game.constants.Command;
import com.foolox.game.core.engin.game.Message;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * comment: 错误信息
 *
 * @author: lipengfei
 * @date: 24/06/2019
 */
@Data
@NoArgsConstructor
public class CommonError implements Message {
    private Command command;
    private String event;
    //错误代码
    private int code;
    //错误信息
    private String info;
}
