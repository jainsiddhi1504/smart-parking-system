package com.siddhi.smartparking.kafka;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class ParkingEventProducer {

    private final KafkaTemplate<String, String> kafkaTemplate;

    public ParkingEventProducer(
            KafkaTemplate<String, String> kafkaTemplate
    ) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendEvent(
            String message
    ) {

        kafkaTemplate.send(
                "parking-events",
                message
        );

        System.out.println(
                "EVENT SENT: " + message
        );
    }
}