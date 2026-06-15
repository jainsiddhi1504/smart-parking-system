package com.siddhi.smartparking.service;

import com.siddhi.smartparking.entity.Vehicle;
import com.siddhi.smartparking.exception.ResourceNotFoundException;
import com.siddhi.smartparking.repository.VehicleRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class VehicleService {

    private final VehicleRepository vehicleRepository;

    public VehicleService(
            VehicleRepository vehicleRepository
    ) {
        this.vehicleRepository = vehicleRepository;
    }

    // Add vehicle
    public Vehicle addVehicle(Vehicle vehicle) {

        vehicle.setParked(false);

        if (vehicleRepository.findByVehicleNumber(
                vehicle.getVehicleNumber()
        ).isPresent()) {

            throw new RuntimeException(
                    "Vehicle number already exists"
            );
        }

        return vehicleRepository.save(vehicle);
    }

    // Get all vehicles
    public List<Vehicle> getAllVehicles() {

        return vehicleRepository.findAll();
    }

    // Get vehicle by ID
    public Vehicle getVehicleById(Long id) {

        return vehicleRepository
                .findById(id)
                .orElseThrow(
                        () -> new ResourceNotFoundException(
                                "Vehicle not found with id: " + id
                        )
                );
    }
}