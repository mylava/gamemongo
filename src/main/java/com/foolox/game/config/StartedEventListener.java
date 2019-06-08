package com.foolox.game.config;

import com.foolox.game.common.model.GameModel;
import com.foolox.game.common.model.GameType;
import com.foolox.game.common.model.Playway;
import com.foolox.game.common.repo.dao.GamePlaywayRepository;
import com.foolox.game.common.repo.dao.SysDicRepository;
import com.foolox.game.common.repo.domain.GamePlayway;
import com.foolox.game.common.repo.domain.SysDic;
import com.foolox.game.common.util.FooloxUtils;
import com.foolox.game.core.FooloxDataContext;
import com.foolox.game.core.engin.game.GameEngine;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.data.domain.Example;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * comment: spring 启动后加载系统资源
 * 在IOC的容器的启动过程，当所有的bean都已经处理完成之后，spring ioc容器会有一个发布事件的动作。
 * 当ioc容器加载处理完相应的bean(即 InitializingBean 接口)之后，给我们提供了一个机会（先有InitializingBean，后有
 * ApplicationListener<ContextRefreshedEvent>），可以去做一些事情。这也是spring ioc容器给提供的一个扩展的地方。
 *
 * @author: lipengfei
 * @date: 20/05/2019
 */
@Slf4j
@Configuration
public class StartedEventListener implements ApplicationListener<ContextRefreshedEvent> {
    @Resource
    private GameEngine gameEngine;
    @Resource
    private SysDicRepository sysDicRepository;
    @Resource
    private GamePlaywayRepository playwayRepository;

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {

        if (FooloxDataContext.getApplicationContext() == null) {
            FooloxDataContext.setApplicationContext(event.getApplicationContext());
        }
        FooloxDataContext.setGameEngine(gameEngine);

        //加载运营机构信息、游戏分类信息
        loadGameModel(loadORGI());
//
    }

    /**
     * 加载运营机构信息
     *
     * @return
     */
    private String loadORGI() {
        SysDic probe = new SysDic();
        probe.setCode(FooloxDataContext.DIC_ORGI);
        Example<SysDic> example = Example.of(probe);
        SysDic sysDic = null;
        try {
            sysDic = sysDicRepository.findOne(example).get();
        } catch (Exception e) {
            log.error("加载运营结构配置失败！");
        }
        if (null != sysDic) {
            //运营机构配置
            FooloxUtils.setDictByCode(sysDic.getCode(), sysDic);
            return sysDic.getId();
        }
        return null;
    }

    /**
     * 加载GameModel信息
     *
     * @param parentId
     */
    private void loadGameModel(String parentId) {
        //根据parentId 查询 gameModel
        SysDic modelProbe = new SysDic();
        modelProbe.setParentId(parentId);
        Example<SysDic> modelExample = Example.of(modelProbe);
        try {
            List<SysDic> list = sysDicRepository.findAll(modelExample);
            if (null != list) {
                List<GameModel> gameModelList = new ArrayList<>();
                for (SysDic model : list) {
                    GameModel gameModel = new GameModel();
                    gameModel.setId(model.getId());
                    gameModel.setName(model.getName());
                    gameModel.setCode(model.getCode());

                    loadGameType(gameModel);
                    gameModelList.add(gameModel);
                    //缓存字典信息
                    FooloxUtils.setDictByCode(gameModel.getCode(), model);
                    //缓存gameModel
                }
                FooloxUtils.setGamesByOrgi(FooloxDataContext.DIC_ORGI, gameModelList);
            }
        } catch (Exception e) {
            log.error("加载GameModel配置失败！");
        }
    }


    /**
     * 加载GameType信息
     *
     * @param gameModel
     */
    private void loadGameType(GameModel gameModel) {
        //根据parentId 查询 gameType
        SysDic probe = new SysDic();
        probe.setParentId(gameModel.getId());
        Example<SysDic> example = Example.of(probe);
        try {
            List<SysDic> list = sysDicRepository.findAll(example);
            if (null != list) {
                List<GameType> gameTypeList = new ArrayList<>();
                for (SysDic type : list) {
                    GameType gameType = new GameType();
                    gameType.setId(type.getId());
                    gameType.setName(type.getName());
                    gameType.setCode(type.getCode());
                    gameType.setModelCode(gameModel.getCode());
                    loadGamePlayWay(gameType);

                    gameTypeList.add(gameType);
                    //缓存字典信息
                    FooloxUtils.setDictByCode(gameModel.getCode(), type);
                }
                gameModel.setGameTypeList(gameTypeList);
            }
        } catch (Exception e) {
            log.error("加载GameModel配置失败！");
        }
    }

    /**
     * 加载GameType信息
     *
     * @param gameType
     */
    private void loadGamePlayWay(GameType gameType) {
        //根据parentId 查询 gameType
        GamePlayway probe = new GamePlayway();
        probe.setModelCode(gameType.getModelCode());
        probe.setTypeCode(gameType.getCode());
        Example<GamePlayway> example = Example.of(probe);
        try {
            List<GamePlayway> list = playwayRepository.findAll(example);
            if (null != list) {
                List<Playway> playwayList = new ArrayList<>();
                for (GamePlayway gamePlayway : list) {
                    Playway playway = new Playway();
                    playway.setId(gamePlayway.getId());
                    playway.setName(gamePlayway.getName());
                    playway.setScore(gamePlayway.getScore());
                    playway.setMinScore(gamePlayway.getMinScore());
                    playway.setMaxScore(gamePlayway.getMaxScore());
                    playway.setLevel(gamePlayway.getLevel());
                    playway.setIconUrl(gamePlayway.getIconUrl());
                    playway.setMemo(gamePlayway.getMemo());
                    playway.setTitle(gamePlayway.getTitle());
                    //STAY 自定义规则，根据需求
//                    playway.setOtherRules(gamePlayway.getOtherRules());
                    playwayList.add(playway);
                    //缓存gamePlayway
                    FooloxUtils.setGamePlaywayById(gamePlayway.getId(), gamePlayway);
                }
                gameType.setPlaywayList(playwayList);
            }
        } catch (Exception e) {
            log.error("加载GameModel配置失败！");
        }
    }

}
