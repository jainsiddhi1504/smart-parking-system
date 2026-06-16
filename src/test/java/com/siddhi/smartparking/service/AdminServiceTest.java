package com.siddhi.smartparking.service;

import com.siddhi.smartparking.dto.DashboardResponse;
import com.siddhi.smartparking.entity.ParkingSlot;
import com.siddhi.smartparking.entity.Vehicle;

import com.siddhi.smartparking.repository.ParkingHistoryRepository;
import com.siddhi.smartparking.repository.ParkingSlotRepository;
import com.siddhi.smartparking.repository.VehicleRepository;

import org.springframework.cache.CacheManager;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.mockito.Mockito.when;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
public class AdminServiceTest {
    @Mock
    private ParkingHistoryRepository parkingHistoryRepository;

    @Mock
    private ParkingSlotRepository parkingSlotRepository;

    @Mock
    private VehicleRepository vehicleRepository;

    @Mock
    private CacheManager cacheManager;

    @InjectMocks
    private AdminService adminService;

    @Test
    void shouldReturnDashboardData() {

        ParkingSlot occupied1 = new ParkingSlot();
        ParkingSlot occupied2 = new ParkingSlot();

        ParkingSlot available1 = new ParkingSlot();
        ParkingSlot available2 = new ParkingSlot();
        ParkingSlot available3 = new ParkingSlot();

        Vehicle v1 = new Vehicle();
        Vehicle v2 = new Vehicle();

        when(parkingHistoryRepository.getTotalRevenue())
                .thenReturn(500.0);

        when(parkingSlotRepository.findByStatus("OCCUPIED"))
                .thenReturn(List.of(occupied1, occupied2));

        when(parkingSlotRepository.findByStatus("AVAILABLE"))
                .thenReturn(
                        List.of(
                                available1,
                                available2,
                                available3
                        )
                );

        when(vehicleRepository.findAll())
                .thenReturn(List.of(v1, v2));

        DashboardResponse response =
                adminService.getDashboard();

        assertEquals(
                500.0,
                response.getTotalRevenue()
        );

        assertEquals(
                2,
                response.getOccupiedSlots()
        );

        assertEquals(
                3,
                response.getAvailableSlots()
        );

        assertEquals(
                2,
                response.getTotalVehiclesParked()
        );

        assertEquals(
                40.0,
                response.getOccupancyPercentage()
        );
    }
    @Test
    void shouldReturnZeroOccupancyWhenNoSlotsExist() {

        when(parkingHistoryRepository.getTotalRevenue())
                .thenReturn(0.0);

        when(parkingSlotRepository.findByStatus("OCCUPIED"))
                .thenReturn(List.of());

        when(parkingSlotRepository.findByStatus("AVAILABLE"))
                .thenReturn(List.of());

        when(vehicleRepository.findAll())
                .thenReturn(List.of());

        DashboardResponse response =
                adminService.getDashboard();

        assertEquals(
                0.0,
                response.getOccupancyPercentage()
        );

        assertEquals(
                0,
                response.getOccupiedSlots()
        );

        assertEquals(
                0,
                response.getAvailableSlots()
        );
    }

}
