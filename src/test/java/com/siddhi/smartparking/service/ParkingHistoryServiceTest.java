package com.siddhi.smartparking.service;

import com.siddhi.smartparking.entity.ParkingHistory;
import com.siddhi.smartparking.repository.ParkingHistoryRepository;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.mockito.ArgumentMatchers.any;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@ExtendWith(MockitoExtension.class)
class ParkingHistoryServiceTest {

    @Mock
    private ParkingHistoryRepository parkingHistoryRepository;

    @InjectMocks
    private ParkingHistoryService parkingHistoryService;

    @Test
    void shouldSaveHistorySuccessfully() {

        ParkingHistory history =
                new ParkingHistory();

        history.setVehicleNumber("WB01AA1111");

        when(parkingHistoryRepository.save(any(ParkingHistory.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        ParkingHistory savedHistory =
                parkingHistoryService.saveHistory(history);

        assertNotNull(savedHistory);
        assertEquals(
                "WB01AA1111",
                savedHistory.getVehicleNumber()
        );
    }
    @Test
    void shouldReturnAllHistory() {

        ParkingHistory history1 =
                new ParkingHistory();

        history1.setVehicleNumber(
                "WB01AA1111"
        );

        ParkingHistory history2 =
                new ParkingHistory();

        history2.setVehicleNumber(
                "WB01AA2222"
        );

        when(parkingHistoryRepository.findAll())
                .thenReturn(
                        List.of(
                                history1,
                                history2
                        )
                );

        List<ParkingHistory> historyList =
                parkingHistoryService.getAllHistory();

        assertEquals(
                2,
                historyList.size()
        );

        assertEquals(
                "WB01AA1111",
                historyList.get(0)
                        .getVehicleNumber()
        );

        assertEquals(
                "WB01AA2222",
                historyList.get(1)
                        .getVehicleNumber()
        );
    }
}