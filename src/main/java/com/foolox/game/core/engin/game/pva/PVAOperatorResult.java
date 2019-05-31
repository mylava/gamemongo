package com.foolox.game.core.engin.game.pva;

import com.foolox.game.common.repo.domain.Player;
import com.foolox.game.constants.Command;
import com.foolox.game.constants.PVAStatusEnum;
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
public class PVAOperatorResult implements Message {
    private String status;    //操作状态 ， 成功|失败|等待|未知|无权限|非法操作|用户不存在
    private long balance;    //余额
    private long amount;    //改变的金额
    private String action;    //操作类型
    private String message;//操作提示消息
    private Command command;//指令
    private String event;

    public PVAOperatorResult(String status, String action, Player player) {
        this(status, action, player, 0, null);
    }

    public PVAOperatorResult(String status, String action, long amount) {
        this(status, action, null, amount, null);
    }

    public PVAOperatorResult(String status, String action, Player player, long amount) {
        this(status, action, player, amount, null);
    }

    /**
     * @param status
     * @param action
     * @param player
     * @param amount
     * @param message
     */
    public PVAOperatorResult(String status, String action, Player player, long amount, String message) {
        this.action = action;
        if (player != null) {
            this.status = status;
            this.balance = player.getCoins();
        } else {
            this.status = PVAStatusEnum.NOTEXIST.toString();
            this.balance = 0;
        }
        this.message = message;
    }

    public void setPlayer(Player player) {
        if (player != null) {
            this.balance = player.getCoins();
        } else {
            this.status = PVAStatusEnum.NOTEXIST.toString();
            this.balance = 0;
        }
    }
}
