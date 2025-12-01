package com.example.delivery.config;

import java.util.Map;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.boot.autoconfigure.kafka.DefaultKafkaConsumerFactoryCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.support.serializer.JsonDeserializer;

@Configuration
public class KafkaConsumerConfig {

    @Bean
    DefaultKafkaConsumerFactoryCustomizer consumerFactoryCustomizer() {
        return factory ->
            factory.updateConfigs(
                Map.of(
                    org.apache.kafka.clients.consumer.ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG,
                    StringDeserializer.class,
                    org.apache.kafka.clients.consumer.ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG,
                    JsonDeserializer.class,
                    JsonDeserializer.TRUSTED_PACKAGES,
                    "*"
                )
            );
    }
}
