package com.siddhi.smartparking.service;

import com.siddhi.smartparking.entity.ParkingHistory;
import com.siddhi.smartparking.entity.ParkingSlot;
import com.siddhi.smartparking.entity.Vehicle;
import com.siddhi.smartparking.entity.WaitingQueue;
import com.siddhi.smartparking.repository.ParkingHistoryRepository;
import com.siddhi.smartparking.repository.ParkingSlotRepository;
import com.siddhi.smartparking.repository.VehicleRepository;
import com.siddhi.smartparking.repository.WaitingQueueRepository;
import org.springframework.scheduling.annotation.Scheduled;
import java.time.Duration;
import org.springframework.stereotype.Service;
import org.springframework.cache.annotation.Cacheable;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ParkingSlotService {

    private final ParkingSlotRepository parkingSlotRepository;
    private final VehicleRepository vehicleRepository;
    private final WaitingQueueRepository waitingQueueRepository;
    private final ParkingHistoryRepository parkingHistoryRepository;


    public ParkingSlotService(
            ParkingSlotRepository parkingSlotRepository,
            VehicleRepository vehicleRepository,
            WaitingQueueRepository waitingQueueRepository,
            ParkingHistoryRepository parkingHistoryRepository
    ) {
        this.parkingSlotRepository = parkingSlotRepository;
        this.vehicleRepository = vehicleRepository;
        this.waitingQueueRepository = waitingQueueRepository;
        this.parkingHistoryRepository =
                parkingHistoryRepository;
    }

    // Add parking slot
    public ParkingSlot addSlot(ParkingSlot parkingSlot) {

        parkingSlot.setStatus("AVAILABLE");

        return parkingSlotRepository.save(parkingSlot);
    }

    // Get all slots
    public List<ParkingSlot> getAllSlots() {

        return parkingSlotRepository.findAll();
    }

    // Book slot remotely
    public ParkingSlot bookSlot(
            Long slotId,
            Long vehicleId
    ) {

        ParkingSlot slot =
                parkingSlotRepository.findById(slotId)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Slot not found"
                                ));

        Vehicle vehicle =
                vehicleRepository.findById(vehicleId)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Vehicle not found"
                                ));

        // Only available slots can be booked
        if (!slot.getStatus().equals("AVAILABLE")) {

            throw new RuntimeException(
                    "Slot not available"
            );
        }

        // Reserve slot
        slot.setStatus("BOOKED");

        // Attach booked vehicle
        slot.setVehicle(vehicle);

        // Store booking time
        slot.setBookingTime(
                LocalDateTime.now()
        );

        return parkingSlotRepository.save(slot);
    }

    public ParkingSlot checkInVehicle(
            Long slotId
    ) {

        ParkingSlot slot =
                parkingSlotRepository.findById(slotId)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Slot not found"
                                ));

        // Slot must already be booked
        if (!slot.getStatus().equals("BOOKED")) {

            throw new RuntimeException(
                    "Slot is not booked"
            );
        }

        Vehicle vehicle = slot.getVehicle();

        if (vehicle == null) {

            throw new RuntimeException(
                    "No vehicle linked to booking"
            );
        }

        // User physically arrived
        slot.setStatus("OCCUPIED");

        // Grace timer stops
        slot.setBookingTime(null);

        // Vehicle parked
        vehicle.setParked(true);

        vehicleRepository.save(vehicle);

        // Create parking history
        ParkingHistory history =
                new ParkingHistory();

        history.setVehicleNumber(
                vehicle.getVehicleNumber()
        );

        history.setOwnerName(
                vehicle.getOwnerName()
        );

        history.setSlotNumber(
                slot.getSlotNumber()
        );

        history.setEntryTime(
                LocalDateTime.now()
        );

        history.setFee(0);

        parkingHistoryRepository.save(history);

        return parkingSlotRepository.save(slot);
    }

    // Release slot
    public ParkingSlot releaseSlot(Long id) {

        ParkingSlot slot = parkingSlotRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Slot not found"));

        // Old vehicle
        Vehicle oldVehicle = slot.getVehicle();

        if (oldVehicle != null) {

            oldVehicle.setParked(false);

            vehicleRepository.save(oldVehicle);

            // Find latest parking history
            ParkingHistory history =
                    parkingHistoryRepository
                            .findTopByVehicleNumberOrderByEntryTimeDesc(
                                    oldVehicle.getVehicleNumber()
                            );

            if (history != null) {

                // Set exit time
                history.setExitTime(
                        LocalDateTime.now()
                );

                // Calculate parking duration
                Duration duration =
                        Duration.between(
                                history.getEntryTime(),
                                history.getExitTime()
                        );

                long hours =
                        Math.max(
                                1,
                                duration.toHours()
                        );

                // ₹20 per hour
                double fee;

                if (hours <= 1) {

                    fee = 20;

                } else {

                    fee = 20 + ((hours - 1) * 30);
                }

                history.setFee(fee);

                parkingHistoryRepository.save(history);
            }
        }

        // Free slot
        slot.setVehicle(null);

        slot.setStatus("AVAILABLE");

        parkingSlotRepository.save(slot);

        // Check waiting queue
        List<WaitingQueue> queue =
                waitingQueueRepository.findAll();

        // Auto assign queue user
        if (!queue.isEmpty()) {

            WaitingQueue waitingUser = queue.get(0);

            Vehicle vehicle =
                    waitingUser.getVehicle();

            // Assign slot
            slot.setVehicle(vehicle);

            slot.setStatus("OCCUPIED");

            // Update vehicle
            vehicle.setParked(true);

            vehicleRepository.save(vehicle);

            // Create parking history
            ParkingHistory history =
                    new ParkingHistory();

            history.setVehicleNumber(
                    vehicle.getVehicleNumber()
            );

            history.setOwnerName(
                    vehicle.getOwnerName()
            );

            history.setSlotNumber(
                    slot.getSlotNumber()
            );

            history.setEntryTime(
                    LocalDateTime.now()
            );

            history.setFee(0);

            parkingHistoryRepository.save(history);

            // Remove from queue
            waitingQueueRepository.deleteById(
                    waitingUser.getId()
            );

            parkingSlotRepository.save(slot);
        }

        return slot;
    }

    // User physically arrives and parks
    public ParkingSlot parkVehicle(
            Long slotId,
            Long vehicleId
    ) {

        ParkingSlot slot = parkingSlotRepository.findById(slotId)
                .orElseThrow(() ->
                        new RuntimeException("Slot not found"));

        Vehicle vehicle = vehicleRepository.findById(vehicleId)
                .orElseThrow(() ->
                        new RuntimeException("Vehicle not found"));

        // Only AVAILABLE or BOOKED slots allowed
        if (slot.getStatus().equals("OCCUPIED")) {

            throw new RuntimeException(
                    "Slot already occupied"
            );
        }

        // Vehicle parked physically
        slot.setStatus("OCCUPIED");

        slot.setBookingTime(null);

        vehicle.setParked(true);

        vehicleRepository.save(vehicle);

        slot.setVehicle(vehicle);

        // Create parking history
        ParkingHistory history =
                new ParkingHistory();

        history.setVehicleNumber(
                vehicle.getVehicleNumber()
        );

        history.setOwnerName(
                vehicle.getOwnerName()
        );

        history.setSlotNumber(
                slot.getSlotNumber()
        );

        history.setEntryTime(
                LocalDateTime.now()
        );

        history.setFee(0);

        parkingHistoryRepository.save(history);

        return parkingSlotRepository.save(slot);
    }

    // Get only available slots
    //@Cacheable("availableSlots")
    public List<ParkingSlot> getAvailableSlots() {

        return parkingSlotRepository
                .findByStatus("AVAILABLE");
    }
    @Scheduled(fixedRate = 60000)
    public void releaseExpiredBookings() {

        List<ParkingSlot> bookedSlots =
                parkingSlotRepository
                        .findByStatusAndBookingTimeIsNotNull(
                                "BOOKED"
                        );

        for (ParkingSlot slot : bookedSlots) {

            Duration duration =
                    Duration.between(
                            slot.getBookingTime(),
                            LocalDateTime.now()
                    );

            // 10 minute grace period
            if (duration.toMinutes() >= 10) {

                slot.setStatus("AVAILABLE");

                slot.setVehicle(null);

                slot.setBookingTime(null);

                parkingSlotRepository.save(slot);

                System.out.println(
                        "Expired booking released for slot: "
                                + slot.getSlotNumber()
                );
            }
        }
    }
    public ParkingSlot searchByVehicleNumber(
            String vehicleNumber
    ) {

        ParkingSlot slot =
                parkingSlotRepository
                        .findByVehicleVehicleNumber(
                                vehicleNumber
                        );

        if (slot == null) {

            throw new RuntimeException(
                    "No booking found for vehicle"
            );
        }

        return slot;
    }
}