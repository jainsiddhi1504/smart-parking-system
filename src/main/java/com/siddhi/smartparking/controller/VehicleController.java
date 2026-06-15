package com.siddhi.smartparking.controller;

import com.siddhi.smartparking.entity.Vehicle;
import com.siddhi.smartparking.service.VehicleService;
import jakarta.validation.Valid;
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
            @Valid
            @RequestBody Vehicle vehicle
    ) {
        return vehicleService.addVehicle(vehicle);
    }

    // Get all vehicles
    @GetMapping
    public List<Vehicle> getAllVehicles() {
        return vehicleService.getAllVehicles();
    }

    // Get vehicle by ID
    @GetMapping("/{id}")
    public Vehicle getVehicleById(
            @PathVariable Long id
    ) {
        return vehicleService.getVehicleById(id);
    }
}