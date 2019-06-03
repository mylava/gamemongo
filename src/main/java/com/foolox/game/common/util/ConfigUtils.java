package com.foolox.game.common.util;

import com.foolox.game.common.repo.dao.AiConfigRepository;
import com.foolox.game.common.repo.domain.AiConfig;
import com.foolox.game.core.FooloxDataContext;
import org.springframework.data.domain.Example;

/**
 * comment:
 *
 * @author: lipengfei
 * @date: 03/06/2019
 */
public class ConfigUtils {

    /**
     * 根据playwayId获取AI配置信息
     * @param playwayId
     * @return
     */
    public static AiConfig getAiConfig(String playwayId) {
        AiConfig config = FooloxUtils.getAiConfigByPlaywayId(playwayId);
        if (config == null) {
            AiConfigRepository aiConfigRes = FooloxDataContext.getApplicationContext().getBean(AiConfigRepository.class);
            AiConfig probe = new AiConfig();
            probe.setPlaywayId(playwayId);
            Example<AiConfig> example = Example.of(probe);
            config = aiConfigRes.findOne(example).get();
            FooloxUtils.setAiConfigByPlaywayId(playwayId, config);
        }
        return config;
    }
}
