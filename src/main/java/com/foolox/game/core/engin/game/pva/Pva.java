package com.foolox.game.core.engin.game.pva;

import com.foolox.game.common.repo.domain.ClientSession;
import com.foolox.game.common.repo.domain.Player;
import com.foolox.game.common.util.FooloxUtils;
import com.foolox.game.constants.PVAStatusEnum;
import com.foolox.game.constants.PVActionEnum;

/**
 * comment:
 * Personal virtual assets(个人虚拟资产) ， 注意：所有的 设计个人账户虚拟资产的 变更的 操作，
 * 都需要先做校验、事务处理和 账号资产状况的 RSA ， PVA信息需要经常和 订单系统 交互
 *
 * @author: lipengfei
 * @date: 30/05/2019
 */
public abstract class Pva {
    /**
     * 充值，返回当前账户余额 , 充值后的业务逻辑处理，如果当前有未处理的订单，需要优先处理
     * @param clientSession
     * @param action , 收入类型 ， 1、充值，2、兑换、3、赢了，4、赠送，6、抽奖，7、接受赠与，8、破产补助
     * @param amount
     * @return
     */
    public abstract PVAOperatorResult income(ClientSession clientSession, String action, long amount);

    /**
     * 消费，返回当前账户余额 ， 消费业务逻辑处理，需要优先验证 当前账户是否有足够的余额用于消费
     * @param clientSession
     * @param action , 支出类型 ， 1、输了，2、逃跑扣除、3、兑换扣除，4、送好友
     * @param amount
     * @return
     */
    public abstract PVAOperatorResult consume(ClientSession clientSession,String action,long amount) ;

    /**
     * 兑换,兑换的业务逻辑处理，需要验证当前账户是否有足够的余额用于 兑换 ， 兑换扣费完成之后，需要 生成新的余额的 签名信息
     * @param clientSession
     * @param amount
     * @param action , 兑换礼品
     * @param giftid
     * @return
     */
    public abstract PVAOperatorResult exchange(ClientSession clientSession ,String action, long amount , String giftid) ;

    /**
     * 验证当前玩家的余额是否有异常
     * @param clientSession
     * @return
     */
    public PVAOperatorResult verify(ClientSession clientSession){
        Player player = playerUser(clientSession);
        /**
         * playUser.getSign();
         */
        return new PVAOperatorResult(PVAStatusEnum.OK.toString(), PVActionEnum.VERIFY.toString(), player);
    }

    /**
     * 获得 GamePlayer
     * @param clientSession
     * @return
     */
    public Player playerUser(ClientSession clientSession){
        Player player = null ;
        if(clientSession!=null){
            player = FooloxUtils.getPlayerBySessionId(clientSession.getId()) ;
        }
        return player ;
    }
}
