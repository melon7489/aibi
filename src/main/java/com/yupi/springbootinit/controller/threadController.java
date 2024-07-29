package com.yupi.springbootinit.controller;


import cn.hutool.json.JSONUtil;
import com.yupi.springbootinit.config.ThreadPoolConfig;
import io.github.classgraph.json.JSONUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * 线程池测试类
 */
@RestController
@Profile({"dev", "local"})
@RequestMapping("/thread")
@Slf4j
public class threadController {
    @Resource
    private ThreadPoolExecutor threadPoolExecutor;

    @GetMapping("/run")
    /**
     * 运行任务
     */
    public void runTask(String taskName){
        CompletableFuture.runAsync(() -> {
            log.info("{}正在运行{}", Thread.currentThread().getName(), taskName);
            try {
                Thread.sleep(60000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }, threadPoolExecutor);
    }
    @GetMapping("/get")
    /**
     * 查看线程池运行状态
     */
    public String monitor(){
        Map<String, Object> map = new ConcurrentHashMap<>();
        map.put("活动线程数：", threadPoolExecutor.getActiveCount());
        map.put("队列长度：", threadPoolExecutor.getQueue().size());
        map.put("当前任务数：", threadPoolExecutor.getTaskCount());
        map.put("已完成任务数：", threadPoolExecutor.getCompletedTaskCount());
        return JSONUtil.toJsonStr(map);
    }
}
