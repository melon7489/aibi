package com.yupi.springbootinit.config;

import lombok.Data;
import org.jetbrains.annotations.NotNull;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

@Configuration
@ConfigurationProperties(prefix = "spring.thread-pool")
@Data
public class ThreadPoolConfig {
    private int corePoolSize;
    private int maximumPoolSize;
    private long keepAliveTime;
    private int capacity;
    private String unit;
    private final ThreadFactory threadFactory = new ThreadFactory() {
        final AtomicInteger count = new AtomicInteger();

        @Override
        public Thread newThread(@NotNull Runnable r) {
            Thread thread = new Thread(r);
            thread.setName("线程：" + count.incrementAndGet());
            return thread;
        }
    };
    @Bean
    public ThreadPoolExecutor threadPoolExecutor(){
        ThreadPoolExecutor threadPoolExecutor =
                new ThreadPoolExecutor(corePoolSize, maximumPoolSize, keepAliveTime, TimeUnit.valueOf(unit), new ArrayBlockingQueue<>(capacity), threadFactory);
        return threadPoolExecutor;
    }
}
