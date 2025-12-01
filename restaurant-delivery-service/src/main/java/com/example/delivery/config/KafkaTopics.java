package com.example.delivery.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class KafkaTopics {

    @Value("${app.topics.deliveryAssigned}")
    String deliveryAssigned;

    @Value("${app.topics.deliveryCompleted}")
    String deliveryCompleted;

    @Bean
    NewTopic deliveryAssignedTopic() {
        return new NewTopic(deliveryAssigned, 3, (short) 1);
    }

    @Bean
    NewTopic deliveryCompletedTopic() {
        return new NewTopic(deliveryCompleted, 3, (short) 1);
    }
}
