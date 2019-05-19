package com.jiuxian.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.config.RetryInterceptorBuilder;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.retry.MessageRecoverer;
import org.springframework.amqp.rabbit.retry.RepublishMessageRecoverer;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.retry.interceptor.StatefulRetryOperationsInterceptor;

/**
 * Author: jiuxian
 * Date: 2019-05-15 23:18:00
 * Comment:
 */

@Configuration
public class RabbitmqConfig {

    public final static String TOPIC_EXCHANGE_ORDER = "order";
    public final static String QUEUE_ORDER_ALL = "order:all";
    public final static String QUEUE_ORDER_DEL = "order:del";

    @Bean
    public MessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public MessageRecoverer messageRecoverer(RabbitTemplate rabbitTemplate) {
        return new RepublishMessageRecoverer(rabbitTemplate, "retry-exchange", "retry.order");
    }

    @Bean
    public Queue orderCreate() {
        return new Queue(QUEUE_ORDER_ALL);
    }

    @Bean
    public TopicExchange topicExchange() {
        return new TopicExchange(TOPIC_EXCHANGE_ORDER);
    }

    @Bean
    public Binding topicBinding1() {
        return BindingBuilder
                .bind(this.orderCreate())
                .to(this.topicExchange())
                .with("order.*");
    }
}
