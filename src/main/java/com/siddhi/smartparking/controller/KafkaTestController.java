package com.siddhi.smartparking.controller;

import com.siddhi.smartparking.kafka.ParkingEventProducer;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class KafkaTestController {

    private final ParkingEventProducer producer;

    public KafkaTestController(
            ParkingEventProducer producer
    ) {
        this.producer = producer;
    }

    @GetMapping("/kafka/test")
    public String testKafka() {

        producer.sendEvent(
                "Vehicle WB24CD5678 parked in Slot 5"
        );

        return "Kafka Event Sent";
    }
}