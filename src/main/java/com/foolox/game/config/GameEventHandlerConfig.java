package com.foolox.game.config;

import com.foolox.game.core.FooloxDataContext;
import com.foolox.game.core.server.CommandHandler;
import com.foolox.game.core.server.FooloxClient;
import com.foolox.game.core.server.GameEventHandler;
import com.foolox.game.core.server.handler.Command;
import com.foolox.game.core.server.handler.EventCommand;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.File;
import java.lang.annotation.Annotation;

/**
 * comment:
 *
 * @author: lipengfei
 * @date: 25/06/2019
 */
@Slf4j
@Configuration
public class GameEventHandlerConfig {
    private ClassLoader classloader = Thread.currentThread().getContextClassLoader();

    @Bean
    public GameEventHandler gameEventHandler() {
        final GameEventHandler handler = new GameEventHandler();
        handlerConfig(handler);
        return handler;
    }

    /**
     * 加载所有 command
     *
     * @return
     */
    public void handlerConfig(GameEventHandler handler) {
        try {
            Class<EventCommand> superClass = (Class<EventCommand>) classloader.loadClass(EventCommand.class.getName());
            File[] resources = FooloxDataContext.getResources(Command.class.getResource("").getPath());
            for (File resource : resources) {
                //载入包下的类
                Class<? extends EventCommand> clazz = (Class<? extends EventCommand>) classloader
                        .loadClass(Command.class.getPackage().getName() + "." + resource.getName().replace(".class", ""));
                if (superClass.isAssignableFrom(clazz) && clazz != superClass) {
                    Command command = handleAnnotation(clazz);
                    if (null != command) {
                        handler.addHandler(CommandHandler.builder()
                                .handler(clazz.newInstance())
                                .method(clazz.getMethod("execute", FooloxClient.class))
                                .command(command.value())
                                .build()
                        );
                    }
                }
            }
        } catch (Exception e) {
            log.error("load commands error, {}", e);
        }

    }

    /**
     * 处理注解，传入一个类，如果是Command注解，则返回该注解
     *
     * @param clazz
     * @return
     */
    private Command handleAnnotation(Class clazz) {
        Annotation[] annotations = clazz.getDeclaredAnnotations();
        if (annotations == null || annotations.length == 0) {
            return null;
        }
        for (int i = 0; i < annotations.length; i++) {
            if (annotations[i] instanceof Command) {
                return (Command) annotations[i];
            }
        }
        return null;
    }
}
