package com.example.restaurant.kitchen_service.kafka.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaTopicsConfig {

    @Bean
    public NewTopic kitchenAccepted() {
        return TopicBuilder.name("kitchen.accepted")
                .partitions(6)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic kitchenCanceled() {
        return TopicBuilder.name("kitchen.canceled")
                .partitions(6)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic kitchenInProgress() {
        return TopicBuilder.name("kitchen.in.progress")
                .partitions(6)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic kitchenEtaUpdated() {
        return TopicBuilder.name("kitchen.eta.updated")
                .partitions(6)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic kitchenOutOfStock() {
        return TopicBuilder.name("kitchen.out.of.stock")
                .partitions(6)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic kitchenPrepared() {
        return TopicBuilder.name("kitchen.prepared")
                .partitions(6)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic kitchenHandedOver() {
        return TopicBuilder.name("kitchen.handed.over")
                .partitions(6)
                .replicas(1)
                .build();
    }
}
