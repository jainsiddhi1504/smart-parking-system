package com.siddhi.smartparking.controller;

import com.siddhi.smartparking.entity.ParkingSlot;
import com.siddhi.smartparking.service.ParkingSlotService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/slots")
public class ParkingSlotController {

    private final ParkingSlotService parkingSlotService;

    public ParkingSlotController(
            ParkingSlotService parkingSlotService
    ) {
        this.parkingSlotService = parkingSlotService;
    }

    // Add slot
    @PostMapping
    public ParkingSlot addSlot(
            @RequestBody ParkingSlot parkingSlot
    ) {
        return parkingSlotService.addSlot(parkingSlot);
    }

    // Get all slots
    @GetMapping
    public List<ParkingSlot> getAllSlots() {
        return parkingSlotService.getAllSlots();
    }

    //Get available slots
    @GetMapping("/available")
    public List <ParkingSlot> getAvailableSlots(){
        return parkingSlotService.getAvailableSlots();
    }
    // Book slot
    @PutMapping("/book/{slotId}/{vehicleId}")
    public ParkingSlot bookSlot(
            @PathVariable Long slotId,
            @PathVariable Long vehicleId
    ) {

        return parkingSlotService.bookSlot(
                slotId,
                vehicleId
        );
    }
    // checkin slot
    @PutMapping("/checkin/{slotId}")
    public ParkingSlot checkInVehicle(
            @PathVariable Long slotId
    ) {

        return parkingSlotService
                .checkInVehicle(slotId);
    }

    // Release slot
    @PutMapping("/release/{id}")
    public ParkingSlot releaseSlot(
            @PathVariable Long id
    ) {
        return parkingSlotService.releaseSlot(id);
    }
    // Park vehicle into slot
    @PutMapping("/park/{slotId}/{vehicleId}")
    public ParkingSlot parkVehicle(
            @PathVariable Long slotId,
            @PathVariable Long vehicleId
    ) {
        return parkingSlotService
                .parkVehicle(slotId, vehicleId);
    }

    @GetMapping("/search/{vehicleNumber}")
    public ParkingSlot searchByVehicleNumber(
            @PathVariable String vehicleNumber
    ) {

        return parkingSlotService
                .searchByVehicleNumber(
                        vehicleNumber
                );
    }

    @PutMapping("/unpark/{slotId}")
    public ParkingSlot unparkSlot(
            @PathVariable Long slotId
    ) {
        return parkingSlotService.unparkSlot(slotId);
    }
}
