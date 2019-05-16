package com.jiuxian.listener;

import com.jiuxian.config.RabbitmqConfig;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;

/**
 * Author: jiuxian
 * Date: 2019-05-16 22:25:00
 * Comment:
 */
@Component
public class OrderQueueListener {

    @RabbitListener(queues = RabbitmqConfig.QUEUE_ORDER_DEL)
    public void orderDel(String message, byte[] messageByte) {
        System.out.println("___________________________________________________");
        System.out.println("orderDel ");
        System.out.println("接收到信息Str:" + message);
        System.out.println("接收到信息byte:" + new String(messageByte, StandardCharsets.UTF_8));
    }

    @RabbitListener(queues = RabbitmqConfig.QUEUE_ORDER_ALL)
    public void orderAll(String message, byte[] messageByte) {
        System.out.println("___________________________________________________");
        System.out.println("orderAll ");
        System.out.println("接收到信息:" + message);
        System.out.println("接收到信息byte:" + new String(messageByte, StandardCharsets.UTF_8));
    }
}
