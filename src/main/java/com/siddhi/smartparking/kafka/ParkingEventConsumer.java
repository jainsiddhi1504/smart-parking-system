package com.siddhi.smartparking.kafka;

import com.siddhi.smartparking.entity.KafkaEventLog;
import com.siddhi.smartparking.repository.KafkaEventLogRepository;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class ParkingEventConsumer {

    private final KafkaEventLogRepository kafkaEventLogRepository;

    public ParkingEventConsumer(
            KafkaEventLogRepository kafkaEventLogRepository
    ) {
        this.kafkaEventLogRepository =
                kafkaEventLogRepository;
    }

    @KafkaListener(
            topics = "parking-events",
            groupId = "smartparking-group"
    )
    public void consume(String message) {

        System.out.println(
                "EVENT RECEIVED: " + message
        );

        KafkaEventLog event =
                new KafkaEventLog();

        event.setEventMessage(message);

        event.setCreatedAt(
                LocalDateTime.now()
        );

        kafkaEventLogRepository.save(event);
    }
}