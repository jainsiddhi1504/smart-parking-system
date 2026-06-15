package com.siddhi.smartparking.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class KafkaConfig {

    @Bean
    public NewTopic parkingTopic() {

        return new NewTopic(
                "parking-events",
                1,
                (short) 1
        );
    }
}