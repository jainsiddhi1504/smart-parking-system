package com.siddhi.smartparking.repository;

import com.siddhi.smartparking.entity.KafkaEventLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface KafkaEventLogRepository
        extends JpaRepository<KafkaEventLog, Long> {
}