package com.jiuxian;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.ResponseEntity;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@SpringBootApplication
@RestController
public class SpringbootRabbitmqApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringbootRabbitmqApplication.class, args);
    }

    @Resource
    private RabbitTemplate rabbitTemplate;

    @GetMapping(value = "/{exchange}/{routingKey}/{message}")
    public ResponseEntity send(@PathVariable String exchange,
                               @PathVariable String routingKey,
                               @PathVariable String message) {
        System.out.println(String.format("routingKey：%s,message:%s", routingKey, message));
        rabbitTemplate.convertAndSend(exchange, routingKey, message);
        return ResponseEntity.ok().build();
    }

}
