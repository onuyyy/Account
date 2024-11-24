package com.example.account.config;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import redis.embedded.RedisServer;

@Configuration
public class LocalRedisConfig {

    // 프로퍼티 값을 주입하는 방법
    // 외부 설정파일에서 값을 가져오는 데 사용
    @Value(("${spring.redis.port}"))
    private int redisPort;

    private RedisServer redisServer;

    // 빈이 초기화된 후에 자동으로 실행되도록
    @PostConstruct
    public void startRedis() {
        redisServer = new RedisServer(redisPort);
        redisServer.start();
    }

    // 빈이 소멸되기 전에 자동으로 실행
    @PreDestroy
    public void stopRedis() {
        if (redisServer != null) {
            redisServer.stop();
        }
    }
}
