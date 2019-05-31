package com.foolox.game.core.engin.game.pva;

import com.foolox.game.common.repo.dao.PlayerRepository;
import com.foolox.game.common.repo.domain.ClientSession;
import com.foolox.game.common.repo.domain.Player;
import com.foolox.game.common.util.FooloxUtils;
import com.foolox.game.constants.PVAStatusEnum;
import com.foolox.game.constants.PVActionEnum;
import com.foolox.game.core.FooloxDataContext;

/**
 * comment:
 *
 * @author: lipengfei
 * @date: 30/05/2019
 */
public class GoldPVAImpl extends Pva {
    /**
     * 充值
     * ClientSession 仅是 PlayUser的部分字段的镜像，操作个人账号，需要先从数据库存储库中取出PlayUser，
     * 然后账号的虚拟资产变化只针对PlayUser操作，操作完成后，赋值到 ClientSession 然后推送给房间的客户端和当前玩家
     */
    @Override
    public PVAOperatorResult income(ClientSession clientSession, String action, long amount) {
        PVAOperatorResult result = new PVAOperatorResult(PVAStatusEnum.OK.toString(), PVActionEnum.INCOME.toString(), amount);
        clientSession = FooloxUtils.getClientSessionById(clientSession.getId());
        /**
         * 不处理AI,clientSession为空表示AI
         */
        if (clientSession != null) {
            Player player = super.playerUser(clientSession);
            if (amount > 0 && player != null) {
                result.setPlayer(player);
                player.setCoins(player.getCoins() + amount);
                result.setBalance(player.getCoins());                    //账户金额变更，需要重新进行RSA签名
                clientSession.setCoins(player.getCoins());

                FooloxUtils.setClientSessionById(clientSession.getUserId(), clientSession);
                FooloxUtils.published(player, FooloxDataContext.getApplicationContext().getBean(PlayerRepository.class));
            }
        }
        return result;
    }

    /**
     * 消费
     */
    @Override
    public PVAOperatorResult consume(ClientSession clientSession, String action, long amount) {
        PVAOperatorResult result = new PVAOperatorResult(PVAStatusEnum.OK.toString(), PVActionEnum.CONSUME.toString(), amount);
        clientSession = FooloxUtils.getClientSessionById(clientSession.getUserId());
        /**
         * 不处理AI
         */
        if (clientSession != null) {
            Player player = super.playerUser(clientSession);
            if (amount > 0 && player != null) {
                result.setPlayer(player);
                if (player.getCoins() >= amount) {
                    player.setCoins(player.getCoins() - amount);
                    clientSession.setCoins(player.getCoins());
                    result.setBalance(player.getCoins());                //账户金额变更，需要重新进行RSA签名
                    FooloxUtils.setClientSessionById(clientSession.getUserId(), clientSession);
                    FooloxUtils.published(player, FooloxDataContext.getApplicationContext().getBean(PlayerRepository.class));
                } else {//
                    result = new PVAOperatorResult(PVAStatusEnum.NOTENOUGH.toString(), PVActionEnum.CONSUME.toString(), player, amount);
                }
            }
        }
        return result;
    }

    /**
     * 兑换，金币兑换 ， 暂时不做实现，无用 ， 金币是最基本消费 资产类型，无兑换功能
     */
    @Override
    public PVAOperatorResult exchange(ClientSession playUserClient, String action, long amount, String giftid) {
        return new PVAOperatorResult(PVAStatusEnum.INVALID.toString(), PVActionEnum.EXCHANGE.toString(), super.playerUser(playUserClient), amount);
    }
}
