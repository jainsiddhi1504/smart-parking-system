package com.siddhi.smartparking.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.mockito.Mockito.doNothing;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
public class WaitingQueueServiceTest {

    @Mock
    private RedisService redisService;

    @InjectMocks
    private WaitingQueueService waitingQueueService;

    @Test
    void shouldAddVehicleToQueue() {

        doNothing().when(redisService)
                .addToQueue("WB01AA1111");

        waitingQueueService
                .addToQueue("WB01AA1111");
    }
    @Test
    void shouldReturnQueue() {

        when(redisService.getQueue())
                .thenReturn(
                        List.of(
                                "WB01AA1111",
                                "WB01AA2222"
                        )
                );

        List<Object> queue =
                waitingQueueService.getQueue();

        assertEquals(2, queue.size());
    }
    @Test
    void shouldReturnFirstUser() {

        when(redisService.getNextVehicle())
                .thenReturn("WB01AA1111");

        String vehicle =
                waitingQueueService.getFirstUser();

        assertEquals(
                "WB01AA1111",
                vehicle
        );
    }
    @Test
    void shouldReturnQueueSize() {

        when(redisService.getQueueSize())
                .thenReturn(2L);

        Long size =
                waitingQueueService.getQueueSize();

        assertEquals(2L, size);
    }
}
