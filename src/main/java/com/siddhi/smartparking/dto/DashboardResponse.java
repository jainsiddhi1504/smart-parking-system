package com.siddhi.smartparking.dto;

public class DashboardResponse {

    private double totalRevenue;

    private long occupiedSlots;

    private long availableSlots;

    private long totalVehiclesParked;

    public double getTotalRevenue() {
        return totalRevenue;
    }

    public void setTotalRevenue(double totalRevenue) {
        this.totalRevenue = totalRevenue;
    }

    public long getOccupiedSlots() {
        return occupiedSlots;
    }

    public void setOccupiedSlots(long occupiedSlots) {
        this.occupiedSlots = occupiedSlots;
    }

    public long getAvailableSlots() {
        return availableSlots;
    }

    public void setAvailableSlots(long availableSlots) {
        this.availableSlots = availableSlots;
    }

    public long getTotalVehiclesParked() {
        return totalVehiclesParked;
    }

    public void setTotalVehiclesParked(
            long totalVehiclesParked
    ) {
        this.totalVehiclesParked = totalVehiclesParked;
    }
}