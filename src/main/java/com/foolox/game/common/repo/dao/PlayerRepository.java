package com.foolox.game.common.repo.dao;

import com.foolox.game.common.repo.domain.Player;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

/**
 * comment:
 *
 * @author: lipengfei
 * @date: 17/05/2019
 */
@Repository
public interface PlayerRepository extends MongoRepository<Player,String> {
}
