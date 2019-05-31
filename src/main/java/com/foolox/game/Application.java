package com.foolox.game;

import com.foolox.game.core.FooloxDataContext;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * comment:
 *
 * @author: lipengfei
 * @date: 05/05/2019
 */
@SpringBootApplication
public class Application {

    public static void main(String[] args) {
        SpringApplication springApplication = new SpringApplication(Application.class) ;
        FooloxDataContext.setApplicationContext(springApplication.run(args));
    }
}
