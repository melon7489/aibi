package com.yupi.springbootinit.manager;


import com.yupi.springbootinit.common.ErrorCode;
import com.yupi.springbootinit.exception.BusinessException;
import com.yupi.springbootinit.exception.ThrowUtils;
import org.elasticsearch.script.BucketAggregationSelectorScript;
import org.redisson.api.RRateLimiter;
import org.redisson.api.RateIntervalUnit;
import org.redisson.api.RateType;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;


/**
 * Redisson 限流器
 */
@Component
public class RedisLimiterManager {
    @Resource
    private RedissonClient redissonClient;

    public void doRateLimiter(String key) {
        //获取限流器
        RRateLimiter rateLimiter = redissonClient.getRateLimiter(key);
        //设置限流器 每秒限制2次
        rateLimiter.trySetRate(RateType.OVERALL, 2, 1, RateIntervalUnit.SECONDS);
        // 失败抛异常
        if (!rateLimiter.tryAcquire()) {
            throw new BusinessException(ErrorCode.TOO_MANY_REQUEST, "请求过于频繁");
        }
    }
}
