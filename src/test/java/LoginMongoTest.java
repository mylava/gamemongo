import com.foolox.game.Application;
import com.foolox.game.common.repo.dao.PlayerRepository;
import com.foolox.game.common.repo.dao.SystemDictRepository;
import com.foolox.game.common.repo.domain.Player;
import com.foolox.game.common.repo.domain.SystemDict;
import com.foolox.game.common.service.PlayerService;
import com.foolox.game.common.service.SystemDictService;
import com.foolox.game.constants.DictType;
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
    private SystemDictRepository systemDictRepository;

    @Autowired
    private PlayerRepository playerRepository;

    @Autowired
    private SystemDictService systemDictService;

    @Test
    public void test() {

//
        Player p = playerService.save(new Player("1","lpf","123456","xx@email.com","男","杀破狼",
                "玩家","13888888888","headimg",1,1000l,
                "个性签名","000001",new Date(),new Date(),new Date(),"memo",new Date(),"openid",0,1000));

//        System.out.println("save p="+p);
////        GamePlayer one = loginService.findPlayerById(p.getId());
//        GamePlayer one = loginService.findPlayerByUsername(p.getUsername());
//        System.out.println("find one="+one);
//
//        List<GamePlayer> maxPlayerNum = loginService.queryAll();
//        System.out.println("queryAll="+maxPlayerNum);

//        initMongo();

        SystemDict foolox = systemDictService.findOneByCodeType("foolox", DictType.GAME_CONFIG);
        System.out.println(foolox);
    }

    /**
     * 初始化配置信息到system_dict
     */
    public void initMongo() {
        /**
         * --------------- ---------------
         * 游戏类型
         * --------------- ---------------
         */
        SystemDict xuezhanmajiang = new SystemDict();
        xuezhanmajiang.setId(4l);
        xuezhanmajiang.setName("血战麻将");
        xuezhanmajiang.setCode("xzmj");
        xuezhanmajiang.setParentId(2l);
        xuezhanmajiang.setDictType(DictType.GAME_TYPE);
        xuezhanmajiang.setCreateTime(new Date());

        SystemDict gangcimajiang = new SystemDict();
        gangcimajiang.setId(5l);
        gangcimajiang.setName("杠次麻将");
        gangcimajiang.setCode("gcmj");
        gangcimajiang.setParentId(2l);
        gangcimajiang.setDictType(DictType.GAME_TYPE);
        gangcimajiang.setCreateTime(new Date());

        SystemDict errendoudizhu = new SystemDict();
        errendoudizhu.setId(6l);
        errendoudizhu.setName("二人斗地主");
        errendoudizhu.setCode("erdz");
        errendoudizhu.setParentId(3l);
        errendoudizhu.setDictType(DictType.GAME_TYPE);
        errendoudizhu.setCreateTime(new Date());

        SystemDict jingdiandoudizhu = new SystemDict();
        jingdiandoudizhu.setId(7l);
        jingdiandoudizhu.setName("经典斗地主");
        jingdiandoudizhu.setCode("jddz");
        jingdiandoudizhu.setParentId(3l);
        jingdiandoudizhu.setDictType(DictType.GAME_TYPE);
        jingdiandoudizhu.setCreateTime(new Date());


        /**
         * --------------- ---------------
         * 拥有的游戏
         * --------------- ---------------
         */
        SystemDict majiang = new SystemDict();
        majiang.setId(2l);
        majiang.setName("麻将");
        majiang.setCode("majiang");
        majiang.setParentId(1l);
        majiang.setDictType(DictType.GAME_MODEL);
        majiang.setCreateTime(new Date());
        majiang.getDicts().add(xuezhanmajiang);
        majiang.getDicts().add(gangcimajiang);


        SystemDict doudizhu = new SystemDict();
        doudizhu.setId(3l);
        doudizhu.setName("斗地主");
        doudizhu.setCode("doudizhu");
        doudizhu.setParentId(1l);
        //大类
        doudizhu.setDictType(DictType.GAME_MODEL);
        doudizhu.setCreateTime(new Date());
        doudizhu.getDicts().add(errendoudizhu);
        doudizhu.getDicts().add(jingdiandoudizhu);


        /**
         * --------------- ---------------
         * 客户
         * --------------- ---------------
         */
        SystemDict systemDict = new SystemDict();
        systemDict.setId(1l);
        systemDict.setName("瓜牛");
        systemDict.setCode("foolox");
        systemDict.setParentId(0l);
        //租户
        systemDict.setDictType(DictType.GAME_CONFIG);
        systemDict.setCreateTime(new Date());
        systemDict.getDicts().add(majiang);
        systemDict.getDicts().add(doudizhu);

        systemDictRepository.save(systemDict);
    }
}
