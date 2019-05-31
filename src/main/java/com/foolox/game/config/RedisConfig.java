package com.foolox.game.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

/**
 * comment:
 *
 * @author: lipengfei
 * @date: 21/08/2018
 */

@Configuration
public class RedisConfig {
    @Value("${spring.redis.host}")
    private String host;
    @Value("${spring.redis.port}")
    private int port;
    @Value("${spring.redis.timeout}")
    private int timeout;
    @Value("${spring.redis.password}")
    private String password;
    @Value("${spring.redis.database}")
    private int database;
    @Value("${spring.redis.jedis.pool.max-active}")
    private int poolMaxActive;
    @Value("${spring.redis.jedis.pool.max-idle}")
    private int poolMaxIdle;
    @Value("${spring.redis.jedis.pool.max-wait}")
    private int poolMaxWait;


    private JedisPoolConfig jedisPoolConfig() {
        JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();
        jedisPoolConfig.setMaxTotal(poolMaxActive);
        jedisPoolConfig.setMaxIdle(poolMaxIdle);
        jedisPoolConfig.setMaxWaitMillis(poolMaxWait);
        return jedisPoolConfig;
    }

    @Bean
    public JedisPool jedispool() {
        return new JedisPool(jedisPoolConfig(),host,port,timeout,password,database);
    }

}
