package com.siddhi.smartparking.repository;

import com.siddhi.smartparking.entity.ParkingSlot;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ParkingSlotRepository
        extends JpaRepository<ParkingSlot, Long> {

    List<ParkingSlot> findByStatus(String status);
    List<ParkingSlot> findByStatusAndBookingTimeIsNotNull(
            String status
    );
    ParkingSlot findByVehicleVehicleNumber(
            String vehicleNumber
    );
}
