package com.siddhi.smartparking.service;

import com.siddhi.smartparking.dto.DashboardResponse;
import com.siddhi.smartparking.repository.ParkingHistoryRepository;
import com.siddhi.smartparking.repository.ParkingSlotRepository;
import com.siddhi.smartparking.repository.VehicleRepository;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service
public class AdminService {

    private final ParkingHistoryRepository parkingHistoryRepository;
    private final ParkingSlotRepository parkingSlotRepository;
    private final VehicleRepository vehicleRepository;
    private final CacheManager cacheManager;

    public AdminService(
            ParkingHistoryRepository parkingHistoryRepository,
            ParkingSlotRepository parkingSlotRepository,
            VehicleRepository vehicleRepository,
            CacheManager cacheManager
    ) {
        this.parkingHistoryRepository = parkingHistoryRepository;
        this.parkingSlotRepository = parkingSlotRepository;
        this.vehicleRepository = vehicleRepository;
        this.cacheManager = cacheManager;
    }

    @Cacheable("dashboard")
    public DashboardResponse getDashboard() {

        System.out.println(
                "CACHE MANAGER = "
                        + cacheManager.getClass().getName()
        );

        System.out.println("INSIDE getDashboard()");

        DashboardResponse response =
                new DashboardResponse();

        Double revenue =
                parkingHistoryRepository.getTotalRevenue();

        response.setTotalRevenue(
                revenue != null ? revenue : 0
        );

        response.setOccupiedSlots(
                parkingSlotRepository
                        .findByStatus("OCCUPIED")
                        .size()
        );

        response.setAvailableSlots(
                parkingSlotRepository
                        .findByStatus("AVAILABLE")
                        .size()
        );

        response.setTotalVehiclesParked(
                vehicleRepository.findAll().size()
        );

        long occupied =
                parkingSlotRepository
                        .findByStatus("OCCUPIED")
                        .size();

        long available =
                parkingSlotRepository
                        .findByStatus("AVAILABLE")
                        .size();

        long totalSlots = occupied + available;

        double occupancyPercentage = 0;

        if (totalSlots > 0) {
            occupancyPercentage =
                    ((double) occupied / totalSlots) * 100;
        }

        response.setOccupancyPercentage(
                occupancyPercentage
        );

        return response;
    }
}