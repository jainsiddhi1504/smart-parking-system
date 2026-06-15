package com.siddhi.smartparking.service;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
public class RedisService {

    private final RedisTemplate<String, Object> redisTemplate;

    private static final String WAITING_QUEUE =
            "waiting_queue";

    public RedisService(
            RedisTemplate<String, Object> redisTemplate
    ) {
        this.redisTemplate = redisTemplate;
    }



    // ==========================
    // JWT Blacklist
    // ==========================

    public void blacklistToken(
            String token
    ) {

        redisTemplate.opsForValue()
                .set(
                        token,
                        "BLACKLISTED",
                        1,
                        TimeUnit.HOURS
                );
    }

    public boolean isBlacklisted(
            String token
    ) {

        return redisTemplate.hasKey(token);
    }

    // ==========================
    // Waiting Queue
    // ==========================

    public void addToQueue(
            String vehicleNumber
    ) {

        redisTemplate.opsForList()
                .rightPush(
                        WAITING_QUEUE,
                        vehicleNumber
                );
    }

    public String getNextVehicle() {

        Object vehicle =
                redisTemplate.opsForList()
                        .leftPop(WAITING_QUEUE);

        return vehicle != null
                ? vehicle.toString()
                : null;
    }

    public Long getQueueSize() {

        return redisTemplate.opsForList()
                .size(WAITING_QUEUE);
    }

    public List<Object> getQueue() {

        return redisTemplate.opsForList()
                .range(
                        WAITING_QUEUE,
                        0,
                        -1
                );
    }

    public void removeFromQueue(String vehicleNumber) {

        redisTemplate.opsForList()
                .remove(
                        WAITING_QUEUE,
                        1,
                        vehicleNumber
                );
    }

    public void clearQueue() {

        redisTemplate.delete(WAITING_QUEUE);
    }

}