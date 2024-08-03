package com.yupi.springbootinit.mq;

import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.util.Scanner;

public class TopicEmitLog {

    private static final String EXCHANGE_NAME = "topic";

    public static void main(String[] argv) throws Exception {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        try (Connection connection = factory.newConnection();
             Channel channel = connection.createChannel()) {
            channel.exchangeDeclare(EXCHANGE_NAME, BuiltinExchangeType.TOPIC);

            String message = "";
            Scanner scanner = new Scanner(System.in);
            while (true) {
                System.out.println("输入消息：");
                message = scanner.nextLine();
                System.out.println("输入路由键：");
                String routingKey = scanner.nextLine();
                channel.basicPublish(EXCHANGE_NAME, routingKey, null, message.getBytes("UTF-8"));
                System.out.println(routingKey + " Sent '" + message + "'");
            }
        }
    }

}
