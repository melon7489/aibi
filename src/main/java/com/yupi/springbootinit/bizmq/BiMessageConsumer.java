package com.yupi.springbootinit.bizmq;

import com.rabbitmq.client.Channel;
import com.yupi.springbootinit.common.ErrorCode;
import com.yupi.springbootinit.constant.BiMqConstant;
import com.yupi.springbootinit.exception.BusinessException;
import com.yupi.springbootinit.exception.ThrowUtils;
import com.yupi.springbootinit.manager.AiManager;
import com.yupi.springbootinit.model.entity.Chart;
import com.yupi.springbootinit.model.enums.ChartStatusEnum;
import com.yupi.springbootinit.service.ChartService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.checkerframework.checker.units.qual.A;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.ChannelCallback;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.IOException;


/**
 * Bi消费者
 */
@Component
@Slf4j
public class BiMessageConsumer {

    @Resource
    ChartService chartService;
    @Resource
    AiManager aiManager;
    @RabbitListener(queues = {BiMqConstant.QUEUE_NAME}, ackMode = "MANUAL")
    public void receiveMessage(String message, Channel channel, @Header(AmqpHeaders.DELIVERY_TAG) long delivery_tag){
        log.info("获取消息:{}", message);
        if (StringUtils.isBlank(message)){
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "消息为空！");
        }
        Chart chart = chartService.getById(Long.parseLong(message));
        if (chart == null){
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "获取chart失败！！");
        }
        log.info("调用AI能力中");
        //修改图标状态为生成中
        Chart runChart = new Chart();
        runChart.setId(chart.getId());
        runChart.setStatus(ChartStatusEnum.RUNNING.getValue());
        boolean b = chartService.updateById(runChart);
        if(!b) handlerChartUpdateError(chart.getId(), "修改图表“生成中”状态失败");

        //给ai提示词并输入数据
        StringBuilder userInput = new StringBuilder();
        userInput.append("分析需求：").append("\n");
        userInput.append(chart.getGoal()).append(",请使用").append(chart.getChartType()).append("\n");
        userInput.append("原始数据：").append("\n").append(chart.getChartData()).append("\n");

        //调用AI能力
        String aiGenRes = null;
        try {
            aiGenRes = aiManager.getAiGenRes(BiMqConstant.MODEL_ID, userInput.toString().trim());
        } catch (Exception e) {
            log.info("调用失败……", e);
        }
        log.info("aiGenRes: {}", aiGenRes);
        String[] split = aiGenRes.split("【【【【【");
        if (split.length < 3) handlerChartUpdateError(chart.getId(), "调用AI能力失败");
        String genChart = split[1].trim();
        String genResult = split[2].trim();

        //成功存入数据库
        Chart newChart = new Chart();
        newChart.setId(chart.getId());
        newChart.setGenChart(genChart);
        newChart.setGenResult(genResult);
        newChart.setStatus(ChartStatusEnum.SUCCEED.getValue());
        boolean update = chartService.updateById(newChart);
        if (!update) handlerChartUpdateError(chart.getId(), "更新图表成功状态失败");
        log.info("调用AI能力结束");
        try {
            channel.basicAck(delivery_tag, false);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    private void handlerChartUpdateError(long chartId, String execMessage){
        Chart chart = new Chart();
        chart.setId(chartId);
        chart.setStatus(ChartStatusEnum.FAILED.getValue());
        chart.setExecMessage(execMessage);
        boolean b = chartService.updateById(chart);
        ThrowUtils.throwIf(!b, ErrorCode.OPERATION_ERROR, "更新图表失败状态失败");
    }
}
