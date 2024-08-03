package com.yupi.springbootinit.bizmq;

import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.yupi.springbootinit.constant.BiMqConstant;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Scanner;
import java.util.concurrent.TimeoutException;

/**
 * 初始化mq队列
 */
public class BiInitMain {
    public static void main(String[] args) {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        try {
            Connection connection = factory.newConnection();
            Channel channel = connection.createChannel();
            channel.exchangeDeclare(BiMqConstant.BI_EXCHANGE_NAME, BuiltinExchangeType.DIRECT);
            channel.queueDeclare(BiMqConstant.QUEUE_NAME, true, false, false, null);
            channel.queueBind(BiMqConstant.QUEUE_NAME, BiMqConstant.BI_EXCHANGE_NAME, BiMqConstant.ROUTING_NAME);
            System.out.println("绑定成功！");
        } catch (Exception e) {
            System.out.println("绑定失败！");
        }finally {
            System.exit(0);
        }
    }
}
