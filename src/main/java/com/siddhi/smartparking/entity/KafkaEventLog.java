package com.siddhi.smartparking.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
public class KafkaEventLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String eventMessage;

    private LocalDateTime createdAt;

    public Long getId() {
        return id;
    }

    public String getEventMessage() {
        return eventMessage;
    }

    public void setEventMessage(String eventMessage) {
        this.eventMessage = eventMessage;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(
            LocalDateTime createdAt
    ) {
        this.createdAt = createdAt;
    }
}