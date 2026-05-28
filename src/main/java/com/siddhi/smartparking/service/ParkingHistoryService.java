package com.siddhi.smartparking.service;

import com.siddhi.smartparking.entity.ParkingHistory;
import com.siddhi.smartparking.repository.ParkingHistoryRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ParkingHistoryService {

    private final ParkingHistoryRepository parkingHistoryRepository;

    public ParkingHistoryService(
            ParkingHistoryRepository parkingHistoryRepository
    ) {
        this.parkingHistoryRepository =
                parkingHistoryRepository;
    }

    // Save history
    public ParkingHistory saveHistory(
            ParkingHistory parkingHistory
    ) {
        return parkingHistoryRepository
                .save(parkingHistory);
    }

    // Get all history
    public List<ParkingHistory> getAllHistory() {

        return parkingHistoryRepository.findAll();
    }
}