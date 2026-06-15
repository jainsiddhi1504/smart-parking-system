package com.siddhi.smartparking.controller;

import com.siddhi.smartparking.service.WaitingQueueService;
import org.springframework.web.bind.annotation.*;
import com.siddhi.smartparking.service.RedisService;

import java.util.List;

@RestController
@RequestMapping("/queue")
public class WaitingQueueController {

    private final WaitingQueueService waitingQueueService;
    private final RedisService redisService;

    public WaitingQueueController(
            WaitingQueueService waitingQueueService,
            RedisService redisService

    ) {
        this.waitingQueueService = waitingQueueService;
        this.redisService=redisService;
    }

    // Add vehicle to queue
    @PostMapping("/{vehicleNumber}")
    public String addToQueue(
            @PathVariable String vehicleNumber
    ) {

        waitingQueueService.addToQueue(vehicleNumber);

        return "Vehicle added to queue";
    }

    // Get entire queue
    @GetMapping
    public List<Object> getQueue() {

        return waitingQueueService.getQueue();
    }

    // Get next vehicle and remove it from queue
    @GetMapping("/next")
    public String getNextVehicle() {

        return waitingQueueService.getFirstUser();
    }

    // Get queue size
    @GetMapping("/size")
    public Long getQueueSize() {

        return waitingQueueService.getQueueSize();
    }

    @DeleteMapping("/queue")
    public String clearQueue() {

        redisService.clearQueue();

        return "Queue cleared";
    }
}