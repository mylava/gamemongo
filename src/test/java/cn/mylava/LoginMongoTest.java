package cn.mylava;

import com.foolox.game.Application;
import com.foolox.game.common.repo.dao.AiConfigRepository;
import com.foolox.game.common.repo.dao.GamePlaywayRepository;
import com.foolox.game.common.repo.dao.PlayerRepository;
import com.foolox.game.common.repo.dao.SysDicRepository;
import com.foolox.game.common.repo.domain.*;
import com.foolox.game.common.service.PlayerService;
import com.foolox.game.common.service.SystemDictService;
import com.foolox.game.constants.DictType;
import com.foolox.game.constants.RoomType;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Date;

/**
 * comment:
 *
 * @author: lipengfei
 * @date: 17/05/2019
 */
@SpringBootTest(classes = {Application.class})
@RunWith(SpringRunner.class)
public class LoginMongoTest {
    @Autowired
    private PlayerService playerService;

    @Autowired
    private SysDicRepository sysDicRepository;

    @Autowired
    private GamePlaywayRepository gamePlaywayRepository;

    @Autowired
    private PlayerRepository playerRepository;

    @Autowired
    private SystemDictService systemDictService;

    @Autowired
    private AiConfigRepository aiConfigRepository;

    @Test
    public void test() {
//        initMongo();
//        initGamePlayway();
//        initAI();

    }

    public void initAI() {
        AiConfig aiConfig = new AiConfig();
        aiConfig.setId("1");
        aiConfig.setPlaywayId("2");
        aiConfig.setEnableai(true);
        aiConfig.setWaittime(10);
        aiConfig.setInitcoins(8000l);
        aiConfig.setExitcon("bank");
        aiConfig.setDicinfo(true);
        aiConfig.setAichat(true);
        aiConfigRepository.save(aiConfig);
    }
    public void initGamePlayway() {
        GamePlayway gamePlayway1 = new GamePlayway();
        gamePlayway1.setId("10");
        gamePlayway1.setName("初级场");
        gamePlayway1.setTitle("初级场");
        gamePlayway1.setModelCode("doudizhu");
        gamePlayway1.setTypeCode("jddz");
        gamePlayway1.setScore(1000);
        gamePlayway1.setMinScore(1000);
        gamePlayway1.setShuffleTimes(1);
        gamePlayway1.setMaxPlayerNum(4);
        gamePlayway1.setMinPlayerNum(2);
        gamePlayway1.setNumOfGames(1);
        gamePlayway1.setRoomtype(RoomType.HALL);
        gamePlaywayRepository.save(gamePlayway1);

        GamePlayway gamePlayway2 = new GamePlayway();
        gamePlayway2.setId("11");
        gamePlayway2.setName("中级场");
        gamePlayway2.setTitle("中级场");
        gamePlayway2.setModelCode("doudizhu");
        gamePlayway2.setTypeCode("jddz");
        gamePlayway2.setScore(5000);
        gamePlayway2.setMinScore(5000);
        gamePlayway2.setShuffleTimes(1);
        gamePlayway2.setMaxPlayerNum(4);
        gamePlayway2.setMinPlayerNum(2);
        gamePlayway2.setNumOfGames(1);
        gamePlayway2.setRoomtype(RoomType.HALL);
        gamePlaywayRepository.save(gamePlayway2);

        GamePlayway gamePlayway3 = new GamePlayway();
        gamePlayway3.setId("12");
        gamePlayway3.setName("高级场");
        gamePlayway3.setTitle("高级场");
        gamePlayway3.setModelCode("doudizhu");
        gamePlayway3.setTypeCode("jddz");
        gamePlayway3.setScore(8000);
        gamePlayway3.setMinScore(8000);
        gamePlayway3.setShuffleTimes(1);
        gamePlayway3.setMaxPlayerNum(4);
        gamePlayway3.setMinPlayerNum(2);
        gamePlayway3.setNumOfGames(1);
        gamePlayway3.setRoomtype(RoomType.HALL);
        gamePlaywayRepository.save(gamePlayway3);
    }

    /**
     * 初始化配置信息到system_dict
     */
    public void initSysdic() {
        /**
         * --------------- ---------------
         * 游戏类型
         * --------------- ---------------
         */
        SysDic xuezhanmajiang = new SysDic();
        xuezhanmajiang.setId("4");
        xuezhanmajiang.setName("血战麻将");
        xuezhanmajiang.setTitle("血战麻将");
        xuezhanmajiang.setCode("xzmj");
        xuezhanmajiang.setParentId("2");
        xuezhanmajiang.setCreatetime(new Date());
        sysDicRepository.save(xuezhanmajiang);


        SysDic gangcimajiang = new SysDic();
        gangcimajiang.setId("5");
        gangcimajiang.setName("杠次麻将");
        gangcimajiang.setTitle("杠次麻将");
        gangcimajiang.setCode("gcmj");
        gangcimajiang.setParentId("2");
        gangcimajiang.setCreatetime(new Date());
        sysDicRepository.save(gangcimajiang);

        SysDic errendoudizhu = new SysDic();
        errendoudizhu.setId("6");
        errendoudizhu.setName("二人斗地主");
        errendoudizhu.setTitle("二人斗地主");
        errendoudizhu.setCode("erdz");
        errendoudizhu.setParentId("3");
        errendoudizhu.setCreatetime(new Date());
        sysDicRepository.save(errendoudizhu);


        SysDic jingdiandoudizhu = new SysDic();
        jingdiandoudizhu.setId("7");
        jingdiandoudizhu.setName("经典斗地主");
        jingdiandoudizhu.setTitle("经典斗地主");
        jingdiandoudizhu.setCode("jddz");
        jingdiandoudizhu.setParentId("3");
        jingdiandoudizhu.setCreatetime(new Date());
        sysDicRepository.save(jingdiandoudizhu);


        /**
         * --------------- ---------------
         * 拥有的游戏
         * --------------- ---------------
         */
        SysDic majiang = new SysDic();
        majiang.setId("2");
        majiang.setName("麻将");
        majiang.setTitle("麻将");
        majiang.setCode("majiang");
        majiang.setParentId("1");
        majiang.setCreatetime(new Date());
        sysDicRepository.save(majiang);


        SysDic doudizhu = new SysDic();
        doudizhu.setId("3");
        doudizhu.setName("斗地主");
        doudizhu.setTitle("斗地主");
        doudizhu.setCode("doudizhu");
        doudizhu.setParentId("1");
        doudizhu.setCreatetime(new Date());
        sysDicRepository.save(doudizhu);


        /**
         * --------------- ---------------
         * 客户
         * --------------- ---------------
         */
        SysDic systemDict = new SysDic();
        systemDict.setId("1");
        systemDict.setName("瓜牛");
        systemDict.setTitle("瓜牛");
        systemDict.setCode("foolox");
        systemDict.setParentId("0");
        systemDict.setCreatetime(new Date());

        sysDicRepository.save(systemDict);
    }
}
