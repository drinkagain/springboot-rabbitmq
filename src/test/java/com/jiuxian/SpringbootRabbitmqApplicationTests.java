package com.jiuxian;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestComponent;
import org.springframework.http.ResponseEntity;
import org.springframework.retry.RetryCallback;
import org.springframework.retry.RetryContext;
import org.springframework.retry.RetryListener;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.UUID;

@RunWith(SpringRunner.class)
@SpringBootTest
public class SpringbootRabbitmqApplicationTests {

    @Test
    public void contextLoads() {
    }


    @Resource
    private RabbitTemplate rabbitTemplate;

    @Test
    public void send() throws InterruptedException {
        String exchange = "order";
        String routingKey = "order.update";
        String message = "Order Update";
        rabbitTemplate.convertAndSend(exchange, routingKey, message);
        Thread.sleep(1000 * 20);
    }

    @Test
    public void del() throws InterruptedException {
        String exchange = "order";
        String routingKey = "order.del";
        String message = "a";
        CorrelationData correlationData = new CorrelationData();
        correlationData.setId("123456789");
        rabbitTemplate.convertAndSend(exchange, routingKey, message, correlationData);
//        Thread.sleep(1000 * 20);
    }

    @Test
    public void sendDel() throws InterruptedException {
        String exchange = "order";
        String routingKey = "order.del";
//        String message = "a";
        CorrelationData correlationData = new CorrelationData();
        correlationData.setId("123456789");
        MessageProperties messageProperties = new MessageProperties();
        messageProperties.setMessageId(UUID.randomUUID().toString());
        Message message = new Message("a".getBytes(), messageProperties);
        rabbitTemplate.send(exchange, routingKey, message, correlationData);
//        Thread.sleep(1000 * 20);
    }

    @Test
    public void delayQueueTest() {
        String exchange = "delay-exchange";
        String routingKey = "order.delay";
        String message = "发送时间：" + LocalDateTime.now();
        MessageProperties properties = new MessageProperties();
        properties.setDelay(1000 * 10);
        rabbitTemplate.convertAndSend(exchange, routingKey, new Message(message.getBytes(), properties));
    }
}

