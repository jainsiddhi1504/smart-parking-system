package com.siddhi.smartparking.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.when;
import static org.mockito.ArgumentMatchers.any;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.springframework.messaging.simp.SimpMessagingTemplate;

import com.siddhi.smartparking.entity.ParkingSlot;
import com.siddhi.smartparking.repository.ParkingSlotRepository;
import com.siddhi.smartparking.repository.VehicleRepository;
import com.siddhi.smartparking.repository.WaitingQueueRepository;
import com.siddhi.smartparking.repository.ParkingHistoryRepository;
import com.siddhi.smartparking.kafka.ParkingEventProducer;
import com.siddhi.smartparking.entity.Vehicle;
import static org.junit.jupiter.api.Assertions.assertThrows;


//import com.siddhi.smartparking.service.EmailService;
//import com.siddhi.smartparking.service.PdfReceiptService;
//import com.siddhi.smartparking.service.RedisService;
//import com.siddhi.smartparking.service.WaitingQueueService;
//import com.siddhi.smartparking.service.ParkingSlotService;
import java.util.List;



@ExtendWith(MockitoExtension.class)
public class ParkingSlotServiceTest {

    @Mock
    private ParkingSlotRepository parkingSlotRepository;

    @Mock
    private VehicleRepository vehicleRepository;

    @Mock
    private WaitingQueueRepository waitingQueueRepository;

    @Mock
    private ParkingHistoryRepository parkingHistoryRepository;

    @Mock
    private ParkingEventProducer parkingEventProducer;

    @Mock
    private EmailService emailService;

    @Mock
    private PdfReceiptService pdfReceiptService;

    @Mock
    private RedisService redisService;

    @Mock
    private WaitingQueueService waitingQueueService;

    @Mock
    private SimpMessagingTemplate messagingTemplate;

    @InjectMocks
    private ParkingSlotService parkingSlotService;

    @Test
    void shouldAddSlotSuccessfully() {

        ParkingSlot slot = new ParkingSlot();
        slot.setSlotNumber("A1");

        when(parkingSlotRepository.save(any(ParkingSlot.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        ParkingSlot savedSlot =
                parkingSlotService.addSlot(slot);

        assertNotNull(savedSlot);
        assertEquals("AVAILABLE", savedSlot.getStatus());
        assertEquals("A1", savedSlot.getSlotNumber());
    }

    @Test
    void shouldReturnAllSlots() {

        ParkingSlot slot1 = new ParkingSlot();
        slot1.setSlotNumber("A1");

        ParkingSlot slot2 = new ParkingSlot();
        slot2.setSlotNumber("A2");

        when(parkingSlotRepository.findAll())
                .thenReturn(List.of(slot1, slot2));

        List<ParkingSlot> slots =
                parkingSlotService.getAllSlots();

        assertEquals(2, slots.size());
        assertEquals("A1", slots.get(0).getSlotNumber());
        assertEquals("A2", slots.get(1).getSlotNumber());
    }
    @Test
    void shouldBookSlotSuccessfully() {

        ParkingSlot slot = new ParkingSlot();
        slot.setSlotNumber("A1");
        slot.setStatus("AVAILABLE");

        Vehicle vehicle = new Vehicle();
        vehicle.setVehicleNumber("KA01AB1234");

        when(parkingSlotRepository.findById(1L))
                .thenReturn(java.util.Optional.of(slot));

        when(vehicleRepository.findById(1L))
                .thenReturn(java.util.Optional.of(vehicle));

        when(parkingSlotRepository.save(any(ParkingSlot.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        ParkingSlot bookedSlot =
                parkingSlotService.bookSlot(1L, 1L);

        assertNotNull(bookedSlot);
        assertEquals("BOOKED", bookedSlot.getStatus());
        assertEquals(vehicle, bookedSlot.getVehicle());
    }

    @Test
    void shouldReturnAvailableSlots() {

        ParkingSlot slot1 = new ParkingSlot();
        slot1.setSlotNumber("A1");
        slot1.setStatus("AVAILABLE");

        ParkingSlot slot2 = new ParkingSlot();
        slot2.setSlotNumber("A2");
        slot2.setStatus("AVAILABLE");

        when(parkingSlotRepository.findByStatus("AVAILABLE"))
                .thenReturn(List.of(slot1, slot2));

        List<ParkingSlot> availableSlots =
                parkingSlotService.getAvailableSlots();

        assertEquals(2, availableSlots.size());
        assertEquals("A1", availableSlots.get(0).getSlotNumber());
        assertEquals("A2", availableSlots.get(1).getSlotNumber());
    }
    @Test
    void shouldThrowExceptionWhenSlotNotAvailable() {

        ParkingSlot slot = new ParkingSlot();
        slot.setStatus("OCCUPIED");

        Vehicle vehicle = new Vehicle();

        when(parkingSlotRepository.findById(1L))
                .thenReturn(java.util.Optional.of(slot));

        when(vehicleRepository.findById(1L))
                .thenReturn(java.util.Optional.of(vehicle));

        RuntimeException exception =
                assertThrows(
                        RuntimeException.class,
                        () -> parkingSlotService.bookSlot(1L, 1L)
                );

        assertEquals(
                "Slot not available",
                exception.getMessage()
        );
    }
    @Test
    void shouldSearchByVehicleNumberSuccessfully() {

        ParkingSlot slot = new ParkingSlot();
        slot.setSlotNumber("A1");

        when(parkingSlotRepository
                .findByVehicleVehicleNumber("KA01AB1234"))
                .thenReturn(slot);

        ParkingSlot foundSlot =
                parkingSlotService.searchByVehicleNumber(
                        "KA01AB1234"
                );

        assertNotNull(foundSlot);
        assertEquals("A1", foundSlot.getSlotNumber());
    }

    @Test
    void shouldThrowExceptionWhenVehicleNotFoundInSearch() {

        when(parkingSlotRepository
                .findByVehicleVehicleNumber("KA01AB1234"))
                .thenReturn(null);

        RuntimeException exception =
                assertThrows(
                        RuntimeException.class,
                        () -> parkingSlotService
                                .searchByVehicleNumber(
                                        "KA01AB1234"
                                )
                );

        assertEquals(
                "No booking found for vehicle",
                exception.getMessage()
        );
    }
    @Test
    void shouldCheckInVehicleSuccessfully() {

        ParkingSlot slot = new ParkingSlot();
        slot.setStatus("BOOKED");
        slot.setSlotNumber("A1");

        Vehicle vehicle = new Vehicle();
        vehicle.setVehicleNumber("KA01AB1234");
        vehicle.setOwnerName("Siddhi");

        slot.setVehicle(vehicle);

        when(parkingSlotRepository.findById(1L))
                .thenReturn(java.util.Optional.of(slot));

        when(vehicleRepository.save(any(Vehicle.class)))
                .thenReturn(vehicle);

        when(parkingSlotRepository.save(any(ParkingSlot.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        ParkingSlot result =
                parkingSlotService.checkInVehicle(1L);

        assertEquals(
                "OCCUPIED",
                result.getStatus()
        );

        assertEquals(
                true,
                vehicle.isParked()
        );
    }
    @Test
    void shouldThrowExceptionWhenCheckingInNonBookedSlot() {

        ParkingSlot slot = new ParkingSlot();
        slot.setStatus("AVAILABLE");

        when(parkingSlotRepository.findById(1L))
                .thenReturn(java.util.Optional.of(slot));

        RuntimeException exception =
                assertThrows(
                        RuntimeException.class,
                        () -> parkingSlotService.checkInVehicle(1L)
                );

        assertEquals(
                "Slot is not booked",
                exception.getMessage()
        );
    }
}