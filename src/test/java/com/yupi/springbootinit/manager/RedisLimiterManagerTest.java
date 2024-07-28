package com.yupi.springbootinit.manager;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class RedisLimiterManagerTest {

    @Resource
    RedisLimiterManager  redisLimiterManager;

    @Test
    void doRateLimiter() {

        for (int i = 0; i < 10; i++) {
            System.out.println("dafaaf");
            redisLimiterManager.doRateLimiter("1");
        }
    }
}