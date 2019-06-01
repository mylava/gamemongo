package com.foolox.game.constants;

/**
 * comment:
 *
 * @author: lipengfei
 * @date: 31/05/2019
 */
public enum MJWinType {
    TUI,
    RIVER,
    END,
    LOST;
    @Override
    public String toString(){
        return super.toString().toLowerCase() ;
    }
}
