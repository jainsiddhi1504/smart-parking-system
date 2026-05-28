package com.siddhi.smartparking.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
public class ParkingSlot {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String slotNumber;

    // AVAILABLE
    // BOOKED
    // OCCUPIED
    private String status;

    private LocalDateTime bookingTime;

    @OneToOne
    private Vehicle vehicle;

    public ParkingSlot() {
    }

    public Long getId() {
        return id;
    }

    public String getSlotNumber() {
        return slotNumber;
    }

    public void setSlotNumber(String slotNumber) {
        this.slotNumber = slotNumber;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Vehicle getVehicle() {
        return vehicle;
    }

    public void setVehicle(Vehicle vehicle) {
        this.vehicle = vehicle;
    }
    public LocalDateTime getBookingTime() {
        return bookingTime;
    }

    public void setBookingTime(
            LocalDateTime bookingTime
    ) {
        this.bookingTime = bookingTime;
    }
}