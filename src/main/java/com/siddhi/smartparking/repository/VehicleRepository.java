package com.siddhi.smartparking.repository;

import com.siddhi.smartparking.entity.Vehicle;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface VehicleRepository
        extends JpaRepository<Vehicle, Long> {
    Optional<Vehicle> findByVehicleNumber(
            String vehicleNumber
    );
}