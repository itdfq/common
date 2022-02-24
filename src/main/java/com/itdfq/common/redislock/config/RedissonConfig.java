package com.itdfq.common.redislock.config;

import org.redisson.Redisson;
import org.redisson.config.Config;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author GodChin
 * @date 2022/2/24 9:48
 * @email 909256107@qq.com
 */
@Configuration
public class RedissonConfig {

    @Autowired
    private RedisProperties redisProperties;


    @Bean
    public Redisson redisson() {
        //单机模式
        // 1.构造redisson实现分布式锁必要的Config
        Config config = new Config();
        config.useSingleServer().setAddress("redis://" + redisProperties.getHost() + ":" +
                redisProperties.getPort()).setPassword(redisProperties.getPassword()).setDatabase(redisProperties.getDatabase());
        // 2.构造RedissonClient
        return (Redisson) Redisson.create(config);
    }
}
