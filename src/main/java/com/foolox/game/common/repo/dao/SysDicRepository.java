package com.foolox.game.common.repo.dao;

import com.foolox.game.common.repo.domain.SysDic;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

/**
 * comment:
 *
 * @author: lipengfei
 * @date: 05/06/2019
 */
@Repository
public interface SysDicRepository extends MongoRepository<SysDic, String> {
}
