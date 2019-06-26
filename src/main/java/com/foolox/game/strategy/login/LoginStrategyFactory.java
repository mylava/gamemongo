package com.foolox.game.strategy.login;

import com.foolox.game.core.FooloxDataContext;
import com.foolox.game.strategy.Organization;

import java.io.File;
import java.io.FileFilter;
import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.List;

/**
 * comment:
 *
 * @author: lipengfei
 * @date: 25/06/2019
 */
public class LoginStrategyFactory {
    private ClassLoader classloader = Thread.currentThread().getContextClassLoader();

    //存储所有策略
    private List<Class<? extends LoginStrategy>> strategyList;

    //静态内部类实现单例模式
    public LoginStrategyFactory() {
        init();
    }

    public static LoginStrategyFactory getInstance() {
        return LoginStrategyFactoryInstance.instance;
    }

    private static class LoginStrategyFactoryInstance {
        private static LoginStrategyFactory instance = new LoginStrategyFactory();
    }

    //在工厂初始化时要初始化策略列表
    private void init() {
        strategyList = new ArrayList<>();
        //获取到包下所有的class文件
        File[] resources = FooloxDataContext.getResources(LoginStrategyFactory.class.getResource("").getPath());
        Class<LoginStrategy> strategyClass = null;
        try {
            strategyClass = (Class<LoginStrategy>) classloader.loadClass(LoginStrategy.class.getName());//使用相同的加载器加载策略接口
        } catch (ClassNotFoundException e1) {
            throw new RuntimeException("未找到策略接口");
        }
        for (int i = 0; i < resources.length; i++) {
            try {
                //载入包下的类
                Class<?> clazz = classloader.loadClass(LoginStrategyFactory.class.getPackage().getName() + "." + resources[i].getName().replace(".class", ""));
                //判断是否是Strategy的实现类并且不是Strategy它本身，满足的话加入到策略列表
                if (LoginStrategy.class.isAssignableFrom(clazz) && clazz != strategyClass) {
                    strategyList.add((Class<? extends LoginStrategy>) clazz);
                }
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 根据客户机构生成相应策略
     *
     * @param
     * @return
     */
    public LoginStrategy createStrategy(String org) {
        //在策略列表查找策略
        for (Class<? extends LoginStrategy> clazz : strategyList) {
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
    private Organization handleAnnotation(Class<? extends LoginStrategy> clazz) {
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
