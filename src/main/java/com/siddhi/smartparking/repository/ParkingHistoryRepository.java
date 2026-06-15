package com.siddhi.smartparking.repository;

import com.siddhi.smartparking.entity.ParkingHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ParkingHistoryRepository
        extends JpaRepository<ParkingHistory, Long> {

    ParkingHistory findTopByVehicleNumberOrderByEntryTimeDesc(
            String vehicleNumber
    );

    @Query(
            "SELECT COALESCE(SUM(p.fee),0) " +
                    "FROM ParkingHistory p"
    )
    Double getTotalRevenue();

    @Query(
            "SELECT COALESCE(SUM(p.fee),0) " +
                    "FROM ParkingHistory p " +
                    "WHERE CAST(p.entryTime AS date) = CURRENT_DATE"
    )
    Double getTodayRevenue();

    long count();
}