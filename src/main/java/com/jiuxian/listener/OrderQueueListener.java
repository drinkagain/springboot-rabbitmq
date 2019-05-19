package com.jiuxian.listener;

import com.jiuxian.config.RabbitmqConfig;
import com.rabbitmq.client.Channel;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.listener.exception.ListenerExecutionFailedException;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.amqp.support.converter.MessageConversionException;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.retry.RetryContext;
import org.springframework.retry.support.RetrySynchronizationManager;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;

/**
 * Author: jiuxian
 * Date: 2019-05-16 22:25:00
 * Comment:
 */
@Component
public class OrderQueueListener {

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = RabbitmqConfig.QUEUE_ORDER_DEL, durable = "true"),
            exchange = @Exchange(value = RabbitmqConfig.TOPIC_EXCHANGE_ORDER,
                    type = ExchangeTypes.TOPIC, ignoreDeclarationExceptions = "true"),
            key = "order.del"))
    public void orderDel(Message messages, Channel channel) throws IOException {
        System.out.println("___________________________________________________");
        System.out.println(messages);
        String message = new String(messages.getBody(), StandardCharsets.UTF_8);
        if ("a".equals(message)) {
            RetryContext context = RetrySynchronizationManager.getContext();
            if (context.getRetryCount() >= 4) {
                long deliveryTag = messages.getMessageProperties().getDeliveryTag();
                channel.basicAck(deliveryTag, false);
            } else {
                System.out.println(LocalDateTime.now());
            }
        } else {
            long deliveryTag = messages.getMessageProperties().getDeliveryTag();
            channel.basicAck(deliveryTag, false);
        }
    }

    @RabbitListener(queues = RabbitmqConfig.QUEUE_ORDER_ALL)
    public void orderAll(String message, Channel channel,
                         @Header(value = AmqpHeaders.DELIVERY_TAG) long deliveryTag) throws IOException {
        System.out.println("___________________________________________________");
        System.out.println("orderAll 接收到信息:" + message);
        channel.basicAck(deliveryTag, false);
    }

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = "retry-queue", durable = "true"),
            exchange = @Exchange(value = "retry-exchange",
                    ignoreDeclarationExceptions = "true",
                    type = ExchangeTypes.TOPIC),
            key = "retry.*"))
    public void retryListener(String message) {
        System.out.println("retry::" + message);
    }

    @RabbitListener(bindings = @QueueBinding(value = @Queue(value = "order-delay", durable = "true"),
            exchange = @Exchange(value = "delay-exchange", type = ExchangeTypes.TOPIC, delayed = Exchange.TRUE),
            key = "order.delay"))
    public void delayQueue(Message messages,
                           Channel channel,
                           @Header(value = AmqpHeaders.DELIVERY_TAG) long deliveryTag) throws IOException {
        System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
        System.out.println(new String(messages.getBody(), StandardCharsets.UTF_8));
        System.out.println("接收时间：" + LocalDateTime.now());
        channel.basicAck(deliveryTag, false);
    }
}
