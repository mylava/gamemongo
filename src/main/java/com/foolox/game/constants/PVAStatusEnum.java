package com.foolox.game.constants;

/**
 * comment:
 *
 * @author: lipengfei
 * @date: 30/05/2019
 */
public enum PVAStatusEnum {
    OK,
    NOTENOUGH,
    FAILD,
    NOTEXIST,
    INVALID;

    @Override
    public String toString() {
        return super.toString().toLowerCase();
    }
}
