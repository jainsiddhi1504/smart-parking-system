package com.siddhi.smartparking.dto;

import java.io.Serializable;

public class DashboardResponse implements Serializable {

    private static final long serialVersionUID = 1L;

    private double totalRevenue;

    private double todayRevenue;

    private double occupancyPercentage;

    private long occupiedSlots;

    private long availableSlots;

    private long totalVehiclesParked;

    private long totalSlots;

    private long queueSize;

    public double getTotalRevenue() {
        return totalRevenue;
    }

    public void setTotalRevenue(double totalRevenue) {
        this.totalRevenue = totalRevenue;
    }

    public double getTodayRevenue() {
        return todayRevenue;
    }

    public void setTodayRevenue(double todayRevenue) {
        this.todayRevenue = todayRevenue;
    }

    public double getOccupancyPercentage() {
        return occupancyPercentage;
    }

    public void setOccupancyPercentage(double occupancyPercentage) {
        this.occupancyPercentage = occupancyPercentage;
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

    public void setTotalVehiclesParked(long totalVehiclesParked) {
        this.totalVehiclesParked = totalVehiclesParked;
    }

    public long getTotalSlots() {
        return totalSlots;
    }

    public void setTotalSlots(long totalSlots) {
        this.totalSlots = totalSlots;
    }

    public long getQueueSize() {
        return queueSize;
    }

    public void setQueueSize(long queueSize) {
        this.queueSize = queueSize;
    }
}