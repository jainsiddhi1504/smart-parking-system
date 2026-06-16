package com.siddhi.smartparking.service;

import com.siddhi.smartparking.dto.DashboardResponse;
import com.siddhi.smartparking.repository.ParkingHistoryRepository;
import com.siddhi.smartparking.repository.ParkingSlotRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.mockito.Mockito.when;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@ExtendWith(MockitoExtension.class)
class DashboardServiceTest {

    @Mock
    private ParkingSlotRepository parkingSlotRepository;

    @Mock
    private ParkingHistoryRepository parkingHistoryRepository;

    @Mock
    private RedisService redisService;

    @InjectMocks
    private DashboardService dashboardService;

    @Test
    void shouldReturnDashboardData() {

        when(parkingSlotRepository.countByStatus("OCCUPIED"))
                .thenReturn(2L);

        when(parkingSlotRepository.countByStatus("AVAILABLE"))
                .thenReturn(3L);

        when(parkingSlotRepository.count())
                .thenReturn(5L);

        when(redisService.getQueueSize())
                .thenReturn(1L);

        when(parkingHistoryRepository.getTotalRevenue())
                .thenReturn(500.0);

        when(parkingHistoryRepository.getTodayRevenue())
                .thenReturn(100.0);

        when(parkingHistoryRepository.count())
                .thenReturn(10L);

        DashboardResponse response=dashboardService.getDashboard();

        assertNotNull(response);

        assertEquals(2L,
                response.getOccupiedSlots());

        assertEquals(3L,
                response.getAvailableSlots());

        assertEquals(5L,
                response.getTotalSlots());

        assertEquals(1L,
                response.getQueueSize());

        assertEquals(500.0,
                response.getTotalRevenue());

        assertEquals(100.0,
                response.getTodayRevenue());

        assertEquals(10L,
                response.getTotalVehiclesParked());

    }

    @Test
    void shouldReturnZeroOccupancyWhenNoSlotsExist() {

        when(parkingSlotRepository.countByStatus("OCCUPIED"))
                .thenReturn(0L);

        when(parkingSlotRepository.countByStatus("AVAILABLE"))
                .thenReturn(0L);

        when(parkingSlotRepository.count())
                .thenReturn(0L);

        when(redisService.getQueueSize())
                .thenReturn(0L);

        when(parkingHistoryRepository.getTotalRevenue())
                .thenReturn(0.0);

        when(parkingHistoryRepository.getTodayRevenue())
                .thenReturn(0.0);

        when(parkingHistoryRepository.count())
                .thenReturn(0L);

        DashboardResponse response =
                dashboardService.getDashboard();
        assertEquals(
                0.0,
                response.getOccupancyPercentage()
        );

        assertEquals(
                0L,
                response.getTotalSlots()
        );

    }

}