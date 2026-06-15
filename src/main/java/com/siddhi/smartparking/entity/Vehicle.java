package com.siddhi.smartparking.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import jakarta.persistence.Column;

@Entity
public class Vehicle {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Vehicle number cannot be blank")
    @Size(
            min = 4,
            max = 20,
            message = "Vehicle number must be between 4 and 20 characters"
    )

    @Column(
            unique=true,
            nullable=false
    )
    private String vehicleNumber;

    @NotBlank(message = "Owner name cannot be blank")
    @Size(
            min = 2,
            max = 50,
            message = "Owner name must be between 2 and 50 characters"
    )
    private String ownerName;
    private String ownerEmail;

    private boolean parked;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getVehicleNumber() {
        return vehicleNumber;
    }

    public void setVehicleNumber(String vehicleNumber) {
        this.vehicleNumber = vehicleNumber;
    }

    public String getOwnerName() {
        return ownerName;
    }

    public void setOwnerName(String ownerName) {
        this.ownerName = ownerName;
    }

    public boolean isParked() {
        return parked;
    }

    public void setParked(boolean parked) {
        this.parked = parked;
    }

    public String getOwnerEmail() {
        return ownerEmail;
    }

    public void setOwnerEmail(String ownerEmail) {
        this.ownerEmail = ownerEmail;
    }
}