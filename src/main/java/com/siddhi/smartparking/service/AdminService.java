package com.siddhi.smartparking.service;

import com.siddhi.smartparking.dto.DashboardResponse;
import com.siddhi.smartparking.repository.ParkingHistoryRepository;
import com.siddhi.smartparking.repository.ParkingSlotRepository;
import com.siddhi.smartparking.repository.VehicleRepository;
import org.springframework.stereotype.Service;

@Service
public class AdminService {

    private final ParkingHistoryRepository parkingHistoryRepository;

    private final ParkingSlotRepository parkingSlotRepository;

    private final VehicleRepository vehicleRepository;

    public AdminService(
            ParkingHistoryRepository parkingHistoryRepository,
            ParkingSlotRepository parkingSlotRepository,
            VehicleRepository vehicleRepository
    ) {
        this.parkingHistoryRepository =
                parkingHistoryRepository;

        this.parkingSlotRepository =
                parkingSlotRepository;

        this.vehicleRepository =
                vehicleRepository;
    }

    public DashboardResponse getDashboard() {

        DashboardResponse response =
                new DashboardResponse();

        // Total revenue
        Double revenue =
                parkingHistoryRepository
                        .getTotalRevenue();

        response.setTotalRevenue(
                revenue != null ? revenue : 0
        );

        // Occupied slots
        response.setOccupiedSlots(
                parkingSlotRepository
                        .findByStatus("OCCUPIED")
                        .size()
        );

        // Available slots
        response.setAvailableSlots(
                parkingSlotRepository
                        .findByStatus("AVAILABLE")
                        .size()
        );

        // Total parked vehicles
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
