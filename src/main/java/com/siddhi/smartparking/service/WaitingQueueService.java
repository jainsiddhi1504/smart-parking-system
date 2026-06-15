package com.siddhi.smartparking.service;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class WaitingQueueService {

    private final RedisService redisService;

    public WaitingQueueService(
            RedisService redisService
    ) {
        this.redisService = redisService;
    }

    // Add vehicle to queue
    public void addToQueue(
            String vehicleNumber
    ) {

        redisService.addToQueue(vehicleNumber);
    }

    // Get entire queue
    public List<Object> getQueue() {

        return redisService.getQueue();
    }

    // Get next vehicle
    public String getFirstUser() {

        return redisService.getNextVehicle();
    }

    // Queue size
    public Long getQueueSize() {

        return redisService.getQueueSize();
    }
}