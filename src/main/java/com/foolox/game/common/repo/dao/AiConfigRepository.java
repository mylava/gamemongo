package com.foolox.game.common.repo.dao;

import com.foolox.game.common.repo.domain.AiConfig;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

/**
 * comment:
 *
 * @author: lipengfei
 * @date: 02/06/2019
 */
@Repository
public interface AiConfigRepository extends MongoRepository<AiConfig,String> {
}
