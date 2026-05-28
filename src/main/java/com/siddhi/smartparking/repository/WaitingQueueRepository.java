package com.siddhi.smartparking.repository;

import com.siddhi.smartparking.entity.WaitingQueue;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WaitingQueueRepository
        extends JpaRepository<WaitingQueue, Long> {
}