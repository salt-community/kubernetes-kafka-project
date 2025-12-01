package com.example.restaurantorderservice.messaging;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaTopicsConfig {

    @Value("${app.topic.order.created}")
    private String orderCreated;

    @Value("${app.topic.order.canceled}")
    private String orderCanceled;


    @Bean
    public NewTopic orderCreated() {
        return TopicBuilder.name(orderCreated)
            .partitions(10)
            .replicas(1)
            .build();
    }

    @Bean
    public NewTopic orderCanceled() {
        return TopicBuilder.name(orderCanceled)
            .partitions(10)
            .replicas(1)
            .build();
    }
}
