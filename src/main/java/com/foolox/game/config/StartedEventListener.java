package com.foolox.game.config;

import com.foolox.game.common.repo.dao.GamePlaywayRepository;
import com.foolox.game.common.repo.domain.GamePlayway;
import com.foolox.game.common.util.redis.RedisService;
import com.foolox.game.common.util.redis.SystemPrefix;
import com.foolox.game.core.FooloxDataContext;
import com.foolox.game.core.engin.game.GameEngine;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;

import javax.annotation.Resource;
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
public class StartedEventListener implements ApplicationListener<ContextRefreshedEvent> {
    @Resource
    private GameEngine gameEngine;

    @Resource
    private GamePlaywayRepository playwayRepository;

    @Resource
    private RedisService redisService;

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {

        if(FooloxDataContext.getApplicationContext() == null){

            FooloxDataContext.setApplicationContext(event.getApplicationContext());
        }
        FooloxDataContext.setGameEngine(gameEngine);

        List<GamePlayway> gamePlaywayList = playwayRepository.findAll() ;
        if(gamePlaywayList != null){
            for(GamePlayway playway : gamePlaywayList){
                redisService.set(SystemPrefix.CONFIG_ID_PLAYWAY, playway.getId(), playway);
            }
        }


//        sysDicRes = event.getApplicationContext().getBean(SysDicRepository.class) ;
//        List<SysDic> sysDicList = sysDicRes.findAll() ;
//
//        for(SysDic dic : sysDicList){
//            CacheHelper.getSystemCacheBean().put(dic.getId(), dic, dic.getOrgi());
//            if(dic.getParentid().equals("0")){
//                List<SysDic> sysDicItemList = new ArrayList<SysDic>();
//                for(SysDic item : sysDicList){
//                    if(item.getDicid()!=null && item.getDicid().equals(dic.getId())){
//                        sysDicItemList.add(item) ;
//                    }
//                }
//                CacheHelper.getSystemCacheBean().put(dic.getCode(), sysDicItemList, dic.getOrgi());
//            }
//        }
//        /**
//         * 加载系统全局配置
//         */
//        SystemConfigRepository systemConfigRes = event.getApplicationContext().getBean(SystemConfigRepository.class) ;
//        SystemConfig config = systemConfigRes.findByOrgi(FooloxDataContext.SYSTEM_ORGI) ;
//        if(config != null){
//            CacheHelper.getSystemCacheBean().put("systemConfig", config, FooloxDataContext.SYSTEM_ORGI);
//        }
//
//
//        GamePlaywayRepository playwayRes = event.getApplicationContext().getBean(GamePlaywayRepository.class) ;
//        List<GamePlayway> gamePlaywayList = playwayRes.findAll() ;
//        if(gamePlaywayList != null){
//            for(GamePlayway playwayId : gamePlaywayList){
//                CacheHelper.getSystemCacheBean().put(playwayId.getId(), playwayId, playwayId.getOrgi());
//            }
//        }
//
//        GameRoomRepository gameRoomRes = event.getApplicationContext().getBean(GameRoomRepository.class) ;
//        List<GameRoom> gameRoomList = gameRoomRes.findAll() ;
//        if(gameRoomList!= null){
//            for(GameRoom gameRoom : gameRoomList){
//                if(gameRoom.isCardroom()){
//                    gameRoomRes.delete(gameRoom);//回收房卡房间资源
//                }else{
//                    CacheHelper.getQueneCache().put(gameRoom, gameRoom.getOrgi());
//                    CacheHelper.getGameRoomCacheBean().put(gameRoom.getId(), gameRoom, gameRoom.getOrgi());
//                }
//            }
//        }
//
//        GenerationRepository generationRes = event.getApplicationContext().getBean(GenerationRepository.class) ;
//        List<Generation> generationList = generationRes.findAll() ;
//        for(Generation generation : generationList){
//            CacheHelper.getSystemCacheBean().setAtomicLong(FooloxDataContext.ModelType.ROOM.toString(), generation.getStartinx());
//        }

    }
}
