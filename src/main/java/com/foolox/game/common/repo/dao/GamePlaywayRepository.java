package com.foolox.game.common.repo.dao;

import com.foolox.game.common.repo.domain.GamePlayway;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

/**
 * comment:
 *
 * @author: lipengfei
 * @date: 20/05/2019
 */
@Repository
public interface GamePlaywayRepository extends MongoRepository<GamePlayway,String> {
}
