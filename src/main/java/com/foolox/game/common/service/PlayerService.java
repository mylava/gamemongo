package com.foolox.game.common.service;

import com.foolox.game.common.repo.dao.PlayerRepository;
import com.foolox.game.common.repo.domain.Player;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * comment:
 *
 * @author: lipengfei
 * @date: 12/05/2019
 */
@Slf4j
@Service
public class PlayerService {

    @Autowired
    private PlayerRepository playerRepository;
    public Player findPlayerById(String id){
        return playerRepository.findById(id).get();
    }

    public Player save(Player player){
        return playerRepository.save(player);
    }

    public List<Player> queryAll(){
        return playerRepository.findAll();
    }

    public Player findPlayerByUsername(String username) {
        Player probe = new Player();
        probe.setUsername(username);
        Example<Player> example = Example.of(probe);
        return playerRepository.findOne(example).get();
    }
}
