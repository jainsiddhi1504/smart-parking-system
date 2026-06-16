package com.siddhi.smartparking.dto;



public class DashboardUpdate {

    private long availableSlots;
    private long occupiedSlots;


    public DashboardUpdate() {
    }

    public DashboardUpdate(long availableSlots, long occupiedSlots) {
        this.availableSlots = availableSlots;
        this.occupiedSlots = occupiedSlots;
    }

    public long getAvailableSlots() {
        return availableSlots;
    }

    public void setAvailableSlots(long availableSlots) {
        this.availableSlots = availableSlots;
    }

    public long getOccupiedSlots() {
        return occupiedSlots;
    }

    public void setOccupiedSlots(long occupiedSlots) {
        this.occupiedSlots = occupiedSlots;
    }
}