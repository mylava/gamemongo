package com.foolox.game.core.engin.game.event;

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
public class BoardRatio implements Message {
    private boolean bomb ;
    private int ratio ;
    private boolean king ;//王炸

    private Command command ;
    private String event ;

    public BoardRatio(boolean bomb ,boolean king , int ratio){
        this.bomb = bomb ;
        this.ratio = ratio ;
        this.king = king ;
    }

}
