package com.foolox.game.core.logic.dizhu;

/**
 * comment:
 *
 * @author: lipengfei
 * @date: 30/05/2019
 */
public enum CardsTypeEnum{
    //斗地主中的牌型
    ONE(1),		//单张      3~K,A,2
    TWO(2),		//一对	 3~K,A,2
    THREE(3),	//三张	 3~K,A,2
    FOUR(4),	//三带一	 AAA+K
    FORMTWO(41),	//三带对	 AAA+K
    FIVE(5),	//单顺	连子		10JQKA
    SIX(6),		//双顺	连对		JJQQKK
    SEVEN(7),	//三顺	飞机		JJJQQQ
    EIGHT(8),	//飞机	带翅膀	JJJ+QQQ+K+A
    EIGHTONE(81),	//飞机	带翅膀	JJJ+QQQ+KK+AA
    NINE(9),	//四带二			JJJJ+Q+K
    NINEONE(91),	//四带二对			JJJJ+QQ+KK
    TEN(10),	//炸弹			JJJJ
    ELEVEN(11);	//王炸			0+0

    private int type ;

    CardsTypeEnum(int type){
        this.type = type ;
    }


    public int getType() {
        return type;
    }


    public void setType(int type) {
        this.type = type;
    }
}