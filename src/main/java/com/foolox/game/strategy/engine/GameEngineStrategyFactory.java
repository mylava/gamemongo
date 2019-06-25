package com.foolox.game.strategy.engine;

import com.foolox.game.strategy.Organization;

import java.io.File;
import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.List;

/**
 * comment:
 *
 * @author: lipengfei
 * @date: 25/06/2019
 */
public class GameEngineStrategyFactory {
    private static final String PACKAGE_PATH = GameEngineStrategyFactory.class.getResource("").getPath();
    private ClassLoader classloader = Thread.currentThread().getContextClassLoader();

    //存储所有策略
    private List<Class<? extends GameEngineStrategy>> strategyList;

    //静态内部类实现单例模式
    public GameEngineStrategyFactory() {
        init();
    }

    public static GameEngineStrategyFactory getInstance() {
        return GameEngineStrategyFactoryInstance.instance;
    }

    private static class GameEngineStrategyFactoryInstance {
        private static GameEngineStrategyFactory instance = new GameEngineStrategyFactory();
    }

    //在工厂初始化时要初始化策略列表
    private void init() {
        strategyList = new ArrayList<>();
        //获取到包下所有的class文件
        File[] resources = getResources();
        Class<GameEngineStrategy> strategyClass = null;
        try {
            strategyClass = (Class<GameEngineStrategy>) classloader.loadClass(GameEngineStrategy.class.getName());//使用相同的加载器加载策略接口
        } catch (ClassNotFoundException e1) {
            throw new RuntimeException("未找到策略接口");
        }
        for (int i = 0; i < resources.length; i++) {
            try {
                //载入包下的类
                Class<?> clazz = classloader.loadClass(GameEngineStrategyFactory.class.getPackage().getName() + "." + resources[i].getName().replace(".class", ""));
                //判断是否是Strategy的实现类并且不是Strategy它本身，满足的话加入到策略列表
                if (GameEngineStrategy.class.isAssignableFrom(clazz) && clazz != strategyClass) {
                    strategyList.add((Class<? extends GameEngineStrategy>) clazz);
                }
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    //获取扫描的包下面所有的class文件
    private File[] getResources() {
//        String path = classloader.getResource(PACKAGE_PATH).getPath();
//        System.out.println(path);
        File file = new File(PACKAGE_PATH);
        return file.listFiles(pathname -> pathname.getName().endsWith(".class"));
    }

    /**
     * 根据客户机构生成相应策略
     *
     * @param
     * @return
     */
    public GameEngineStrategy createStrategy(String org) {
        //在策略列表查找策略
        for (Class<? extends GameEngineStrategy> clazz : strategyList) {
            Organization organization = handleAnnotation(clazz);//获取该策略的注解
            //判断金额是否在注解的区间
            if (organization.name().equals(org)) {
                try {
                    //是的话返回一个当前策略的实例
                    return clazz.newInstance();
                } catch (Exception e) {
                    throw new RuntimeException("策略获得失败");
                }
            }
        }
        throw new RuntimeException("策略获得失败");
    }

    /**
     * 处理注解，传入一个策略类，返回它的注解
     *
     * @param clazz
     * @return
     */
    private Organization handleAnnotation(Class<? extends GameEngineStrategy> clazz) {
        Annotation[] annotations = clazz.getDeclaredAnnotations();
        if (annotations == null || annotations.length == 0) {
            return null;
        }
        for (int i = 0; i < annotations.length; i++) {
            if (annotations[i] instanceof Organization) {
                return (Organization) annotations[i];
            }
        }
        return null;
    }
}
