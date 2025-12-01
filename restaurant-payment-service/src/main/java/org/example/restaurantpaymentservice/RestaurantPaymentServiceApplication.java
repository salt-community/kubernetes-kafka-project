package org.example.restaurantpaymentservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.kafka.annotation.EnableKafka;

@SpringBootApplication
public class RestaurantPaymentServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(RestaurantPaymentServiceApplication.class, args);
    }

}
