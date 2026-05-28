package com.siddhi.smartparking.service;

import com.siddhi.smartparking.entity.WaitingQueue;
import com.siddhi.smartparking.repository.WaitingQueueRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class WaitingQueueService {

    private final WaitingQueueRepository waitingQueueRepository;

    public WaitingQueueService(
            WaitingQueueRepository waitingQueueRepository
    ) {
        this.waitingQueueRepository = waitingQueueRepository;
    }

    // Add user to queue
    public WaitingQueue addToQueue(
            WaitingQueue waitingQueue
    ) {
        return waitingQueueRepository.save(waitingQueue);
    }

    // Get all queue users
    public List<WaitingQueue> getQueue() {

        return waitingQueueRepository.findAll();
    }

    // Get first waiting user
    public WaitingQueue getFirstUser() {

        List<WaitingQueue> queue =
                waitingQueueRepository.findAll();

        if (queue.isEmpty()) {
            return null;
        }

        return queue.get(0);
    }

    // Remove user from queue
    public void removeFromQueue(Long id) {

        waitingQueueRepository.deleteById(id);
    }
}