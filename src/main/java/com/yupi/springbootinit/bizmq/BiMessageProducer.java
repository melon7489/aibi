package com.yupi.springbootinit.bizmq;

import com.yupi.springbootinit.constant.BiMqConstant;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * MQ生产者
 */
@Component
public class BiMessageProducer {
    @Resource
    RabbitTemplate rabbitTemplate;

    /**
     * 消息发送
     * @param message
     */
    public void sendMessage(String message){
        rabbitTemplate.convertAndSend(BiMqConstant.BI_EXCHANGE_NAME, BiMqConstant.ROUTING_NAME,message);
    }
}
