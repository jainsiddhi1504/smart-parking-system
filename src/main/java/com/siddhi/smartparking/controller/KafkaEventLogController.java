package com.siddhi.smartparking.controller;

import com.siddhi.smartparking.entity.KafkaEventLog;
import com.siddhi.smartparking.repository.KafkaEventLogRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/events")
public class KafkaEventLogController {

    private final KafkaEventLogRepository kafkaEventLogRepository;

    public KafkaEventLogController(
            KafkaEventLogRepository kafkaEventLogRepository
    ) {
        this.kafkaEventLogRepository = kafkaEventLogRepository;
    }

    @GetMapping
    public List<KafkaEventLog> getAllEvents() {

        return kafkaEventLogRepository.findAll();
    }
}