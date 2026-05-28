package com.siddhi.smartparking.controller;

import com.siddhi.smartparking.entity.WaitingQueue;
import com.siddhi.smartparking.service.WaitingQueueService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/queue")
public class WaitingQueueController {

    private final WaitingQueueService waitingQueueService;

    public WaitingQueueController(
            WaitingQueueService waitingQueueService
    ) {
        this.waitingQueueService = waitingQueueService;
    }

    // Add user to waiting queue
    @PostMapping
    public WaitingQueue addToQueue(
            @RequestBody WaitingQueue waitingQueue
    ) {
        return waitingQueueService.addToQueue(waitingQueue);
    }

    // Get waiting queue
    @GetMapping
    public List<WaitingQueue> getQueue() {

        return waitingQueueService.getQueue();
    }
}