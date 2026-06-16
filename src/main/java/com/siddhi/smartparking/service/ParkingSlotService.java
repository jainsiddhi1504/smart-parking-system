package com.siddhi.smartparking.service;

import com.siddhi.smartparking.entity.ParkingHistory;
import com.siddhi.smartparking.entity.ParkingSlot;
import com.siddhi.smartparking.entity.Vehicle;
import com.siddhi.smartparking.entity.WaitingQueue;
import com.siddhi.smartparking.kafka.ParkingEventProducer;
import com.siddhi.smartparking.repository.ParkingHistoryRepository;
import com.siddhi.smartparking.repository.ParkingSlotRepository;
import com.siddhi.smartparking.repository.VehicleRepository;
import com.siddhi.smartparking.repository.WaitingQueueRepository;
import org.springframework.scheduling.annotation.Scheduled;
import java.time.Duration;
import org.springframework.stereotype.Service;
import com.siddhi.smartparking.service.EmailService;
import org.springframework.cache.annotation.Cacheable;
import java.io.File;
import com.siddhi.smartparking.service.PdfReceiptService;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import com.siddhi.smartparking.dto.DashboardUpdate;

@Service
public class ParkingSlotService {

    private final ParkingSlotRepository parkingSlotRepository;
    private final VehicleRepository vehicleRepository;
    private final WaitingQueueRepository waitingQueueRepository;
    private final ParkingHistoryRepository parkingHistoryRepository;
    private final ParkingEventProducer parkingEventProducer;
    private final EmailService emailService;
    private final PdfReceiptService pdfReceiptService;
    private final RedisService redisService;
    private final WaitingQueueService waitingQueueService;
    private final SimpMessagingTemplate messagingTemplate;

    public ParkingSlotService(
            ParkingSlotRepository parkingSlotRepository,
            VehicleRepository vehicleRepository,
            WaitingQueueRepository waitingQueueRepository,
            ParkingHistoryRepository parkingHistoryRepository,
            ParkingEventProducer parkingEventProducer,
            EmailService emailService,
            PdfReceiptService pdfReceiptService,
            RedisService redisService,
            WaitingQueueService waitingQueueService,
            SimpMessagingTemplate messagingTemplate

    ) {
        this.parkingSlotRepository = parkingSlotRepository;
        this.vehicleRepository = vehicleRepository;
        this.waitingQueueRepository = waitingQueueRepository;
        this.parkingHistoryRepository = parkingHistoryRepository;
        this.parkingEventProducer = parkingEventProducer;
        this.emailService = emailService;
        this.pdfReceiptService = pdfReceiptService;
        this.redisService=redisService;
        this.waitingQueueService=waitingQueueService;
        this.messagingTemplate = messagingTemplate;
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

        parkingEventProducer.sendEvent(
                "SLOT_BOOKED : "
                        + slot.getSlotNumber()
                        + " BY "
                        + vehicle.getVehicleNumber()
        );

        ParkingSlot savedSlot=parkingSlotRepository.save(slot);

        sendDashboardUpdate();

        return savedSlot;
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

                // ₹20 first hour, ₹30 every additional hour
                double fee;

                if (hours <= 1) {

                    fee = 20;

                } else {

                    fee = 20 + ((hours - 1) * 30);
                }

                history.setFee(fee);

                parkingHistoryRepository.save(history);

                parkingEventProducer.sendEvent(
                        "VEHICLE_EXITED : "
                                + oldVehicle.getVehicleNumber()
                                + " FEE = ₹" + fee
                );

                if (oldVehicle.getOwnerEmail() != null
                        && !oldVehicle.getOwnerEmail().isBlank()) {

                    try {

                        File receipt =
                                pdfReceiptService.generateReceipt(
                                        oldVehicle.getVehicleNumber(),
                                        oldVehicle.getOwnerName(),
                                        slot.getSlotNumber(),
                                        fee
                                );

                        emailService.sendEmailWithAttachment(
                                oldVehicle.getOwnerEmail(),
                                "Parking Receipt",
                                "Please find your parking receipt attached.",
                                receipt
                        );

                    } catch (Exception e) {

                        e.printStackTrace();
                    }
                }
            }
        }

        // Free slot
        slot.setVehicle(null);

        slot.setStatus("AVAILABLE");

        parkingSlotRepository.save(slot);

        // Check Redis waiting queue
        String nextVehicleNumber =
                redisService.getNextVehicle();

        if (nextVehicleNumber != null) {

            System.out.println("NEXT VEHICLE = " + nextVehicleNumber);

            parkingEventProducer.sendEvent(
                    "SLOT_AVAILABLE_FOR:" + nextVehicleNumber
            );

            System.out.println(
                    "NOTIFIED WAITING VEHICLE: "
                            + nextVehicleNumber
            );

            Vehicle vehicle =
                    vehicleRepository
                            .findByVehicleNumber(
                                    nextVehicleNumber
                            )
                            .orElseThrow(
                                    () -> new RuntimeException(
                                            "Vehicle not found"
                                    )
                            );

            System.out.println("FOUND VEHICLE = " + vehicle.getVehicleNumber());

            // Assign slot automatically
            slot.setVehicle(vehicle);

            slot.setStatus("OCCUPIED");

            vehicle.setParked(true);

            vehicleRepository.save(vehicle);



            // Create new parking history
            ParkingHistory newHistory =
                    new ParkingHistory();

            newHistory.setVehicleNumber(
                    vehicle.getVehicleNumber()
            );

            newHistory.setOwnerName(
                    vehicle.getOwnerName()
            );

            newHistory.setSlotNumber(
                    slot.getSlotNumber()
            );

            newHistory.setEntryTime(
                    LocalDateTime.now()
            );

            newHistory.setFee(0);

            parkingHistoryRepository.save(newHistory);

            parkingSlotRepository.save(slot);

            System.out.println("SLOT ASSIGNED");

            parkingEventProducer.sendEvent(
                    "VEHICLE_AUTO_ASSIGNED : "
                            + vehicle.getVehicleNumber()
                            + " -> SLOT "
                            + slot.getSlotNumber()
            );
        }

        sendDashboardUpdate();

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

        redisService.removeFromQueue(
                vehicle.getVehicleNumber()
        );

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

        parkingEventProducer.sendEvent(
                "VEHICLE_PARKED : "
                        + vehicle.getVehicleNumber()
        );

        if (vehicle.getOwnerEmail() != null
                && !vehicle.getOwnerEmail().isBlank()) {

            System.out.println(
                    "EMAIL CHECK: " + vehicle.getOwnerEmail()
            );

            emailService.sendEmail(
                    vehicle.getOwnerEmail(),
                    "Parking Confirmation",
                    "Hello " + vehicle.getOwnerName()
                            + ", your vehicle "
                            + vehicle.getVehicleNumber()
                            + " has been parked successfully in slot "
                            + slot.getSlotNumber()
            );
        }

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

    public ParkingSlot unparkSlot(Long slotId) {

        ParkingSlot slot = parkingSlotRepository.findById(slotId)
                .orElseThrow(() ->
                        new RuntimeException("Slot not found"));

        if (!slot.getStatus().equals("OCCUPIED")) {
            throw new RuntimeException(
                    "Slot is already free"
            );
        }

        Vehicle vehicle = slot.getVehicle();

        slot.setStatus("AVAILABLE");
        slot.setVehicle(null);
        slot.setBookingTime(null);

        parkingEventProducer.sendEvent(
                "SLOT_AVAILABLE : "
                        + slot.getSlotNumber()
        );

        String nextVehicle = waitingQueueService.getFirstUser();

        if (nextVehicle != null) {

            parkingEventProducer.sendEvent(

                    "SLOT_AVAILABLE_FOR:" + nextVehicle

            );

            System.out.println(

                    "NOTIFIED WAITING VEHICLE: " + nextVehicle

            );

        }

        return parkingSlotRepository.save(slot);
    }

    private void sendDashboardUpdate() {

        long available =
                parkingSlotRepository.countByStatus("AVAILABLE");

        long occupied =
                parkingSlotRepository.countByStatus("OCCUPIED");

        DashboardUpdate update =
                new DashboardUpdate(available, occupied);

        messagingTemplate.convertAndSend(
                "/topic/dashboard",
                update
        );
    }

}