package com.foolox.game.core.engin.game.event;

import com.foolox.game.constants.Command;
import com.foolox.game.core.engin.game.Message;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * comment:
 *
 * @author: lipengfei
 * @date: 31/05/2019
 */
@Data
@NoArgsConstructor
public class SearchRoomResult implements Message {
    private String id ;		//玩法ID
    private String code ;	//游戏类型
    private String roomid ; //房间ID
    private String result ;	//
    private Command command ;
    private String event ;

    public SearchRoomResult(String result){
        this.result = result ;
    }

    public SearchRoomResult(String id , String code ,String result){
        this.id = id;
        this.code = code;
        this.result = result;
    }
}
