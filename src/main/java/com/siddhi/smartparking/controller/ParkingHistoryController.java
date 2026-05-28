package com.siddhi.smartparking.controller;

import com.siddhi.smartparking.entity.ParkingHistory;
import com.siddhi.smartparking.service.ParkingHistoryService;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@RestController
@RequestMapping("/history")
public class ParkingHistoryController {

    private final ParkingHistoryService parkingHistoryService;

    public ParkingHistoryController(
            ParkingHistoryService parkingHistoryService
    ) {
        this.parkingHistoryService =
                parkingHistoryService;
    }

    // Save history
    @PostMapping
    public ParkingHistory saveHistory(
            @RequestBody ParkingHistory parkingHistory
    ) {
        return parkingHistoryService
                .saveHistory(parkingHistory);
    }

    // Get all parking history
    @GetMapping
    public List<ParkingHistory> getAllHistory() {

        return parkingHistoryService
                .getAllHistory();
    }
}