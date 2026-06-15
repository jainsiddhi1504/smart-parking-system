package com.siddhi.smartparking.service;

import com.siddhi.smartparking.dto.DashboardResponse;
import com.siddhi.smartparking.repository.ParkingHistoryRepository;
import com.siddhi.smartparking.repository.ParkingSlotRepository;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service
public class DashboardService {

    private final ParkingSlotRepository parkingSlotRepository;
    private final ParkingHistoryRepository parkingHistoryRepository;
    private final RedisService redisService;

    public DashboardService(
            ParkingSlotRepository parkingSlotRepository,
            ParkingHistoryRepository parkingHistoryRepository,
            RedisService redisService
    ) {
        this.parkingSlotRepository = parkingSlotRepository;
        this.parkingHistoryRepository = parkingHistoryRepository;
        this.redisService = redisService;
    }

    //@Cacheable("dashboard")
    public DashboardResponse getDashboard() {

        System.out.println(
                "Occupied = " +
                        parkingSlotRepository.countByStatus("OCCUPIED")
        );

        System.out.println(
                "Available = " +
                        parkingSlotRepository.countByStatus("AVAILABLE")
        );

        System.out.println(
                "Total = " +
                        parkingSlotRepository.count()
        );

        DashboardResponse response =
                new DashboardResponse();

        long occupiedSlots =
                parkingSlotRepository.countByStatus(
                        "OCCUPIED"
                );

        long availableSlots =
                parkingSlotRepository.countByStatus(
                        "AVAILABLE"
                );

        long totalSlots =
                parkingSlotRepository.count();

        Long queueSize =
                redisService.getQueueSize();

        double occupancyPercentage = 0;

        if (totalSlots > 0) {
            occupancyPercentage =
                    ((double) occupiedSlots / totalSlots) * 100;
        }

        Double totalRevenue =
                parkingHistoryRepository.getTotalRevenue();

        Double todayRevenue =
                parkingHistoryRepository.getTodayRevenue();

        long totalVehiclesParked =
                parkingHistoryRepository.count();

        response.setOccupiedSlots(
                occupiedSlots
        );

        response.setAvailableSlots(
                availableSlots
        );

        response.setOccupancyPercentage(
                occupancyPercentage
        );

        response.setTotalRevenue(
                totalRevenue != null ? totalRevenue : 0
        );

        response.setTodayRevenue(
                todayRevenue != null ? todayRevenue : 0
        );

        response.setTotalVehiclesParked(
                totalVehiclesParked
        );

        response.setTotalSlots(
                totalSlots
        );

        response.setQueueSize(
                queueSize != null ? queueSize : 0
        );

        return response;
    }
}