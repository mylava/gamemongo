package com.foolox.game.common.repo.dao;

import com.foolox.game.common.repo.domain.GameRoom;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

/**
 * comment:
 *
 * @author: lipengfei
 * @date: 26/05/2019
 */
@Repository
public interface GameRoomRepository  extends MongoRepository<GameRoom,String> {
}
