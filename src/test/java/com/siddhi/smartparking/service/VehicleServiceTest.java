package com.siddhi.smartparking.service;

import com.siddhi.smartparking.entity.Vehicle;
import com.siddhi.smartparking.repository.VehicleRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.Optional;
import static org.mockito.Mockito.when;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import com.siddhi.smartparking.exception.ResourceNotFoundException;
@ExtendWith(MockitoExtension.class)
class VehicleServiceTest {

    @Mock
    private VehicleRepository vehicleRepository;

    @InjectMocks
    private VehicleService vehicleService;

    @Test
    void shouldAddVehicleSuccessfully() {

        Vehicle vehicle = new Vehicle();

        vehicle.setVehicleNumber("WB01AA1111");

        when(vehicleRepository.findByVehicleNumber("WB01AA1111"))
                .thenReturn(Optional.empty());

        when(vehicleRepository.save(vehicle))
                .thenReturn(vehicle);

        Vehicle savedVehicle =
                vehicleService.addVehicle(vehicle);

        assertNotNull(savedVehicle);
        assertFalse(savedVehicle.isParked());
    }
    @Test
    void shouldThrowExceptionWhenVehicleAlreadyExists() {

        Vehicle vehicle = new Vehicle();

        vehicle.setVehicleNumber("WB01AA1111");

        when(vehicleRepository.findByVehicleNumber("WB01AA1111"))
                .thenReturn(Optional.of(vehicle));

        RuntimeException exception =
                assertThrows(
                        RuntimeException.class,
                        () -> vehicleService.addVehicle(vehicle)
                );

        assertEquals(
                "Vehicle number already exists",
                exception.getMessage()
        );
    }
    @Test
    void shouldGetVehicleById() {

        Vehicle vehicle = new Vehicle();

        vehicle.setId(1L);
        vehicle.setVehicleNumber("WB01AA1111");

        when(vehicleRepository.findById(1L))
                .thenReturn(Optional.of(vehicle));

        Vehicle foundVehicle =
                vehicleService.getVehicleById(1L);

        assertNotNull(foundVehicle);
        assertEquals(1L, foundVehicle.getId());
    }

    @Test
    void shouldThrowExceptionWhenVehicleNotFound() {

        when(vehicleRepository.findById(1L))
                .thenReturn(Optional.empty());

        assertThrows(
                ResourceNotFoundException.class,
                () -> vehicleService.getVehicleById(1L)
        );
    }
}
