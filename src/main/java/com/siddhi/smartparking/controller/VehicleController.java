package com.siddhi.smartparking.controller;

import com.siddhi.smartparking.entity.Vehicle;
import com.siddhi.smartparking.service.VehicleService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/vehicles")
public class VehicleController {

    private final VehicleService vehicleService;

    public VehicleController(
            VehicleService vehicleService
    ) {
        this.vehicleService = vehicleService;
    }

    // Add vehicle
    @PostMapping
    public Vehicle addVehicle(
            @RequestBody Vehicle vehicle
    ) {
        return vehicleService.addVehicle(vehicle);
    }

    // Get all vehicles
    @GetMapping
    public List<Vehicle> getAllVehicles() {
        return vehicleService.getAllVehicles();
    }
}