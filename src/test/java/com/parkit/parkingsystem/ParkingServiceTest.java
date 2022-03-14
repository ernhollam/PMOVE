package com.parkit.parkingsystem;

import com.parkit.parkingsystem.constants.ParkingType;
import com.parkit.parkingsystem.dao.ParkingSpotDAO;
import com.parkit.parkingsystem.dao.TicketDAO;
import com.parkit.parkingsystem.model.ParkingSpot;
import com.parkit.parkingsystem.model.Ticket;
import com.parkit.parkingsystem.service.ParkingService;
import com.parkit.parkingsystem.util.InputReaderUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatExceptionOfType;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ParkingServiceTest {

    private static ParkingService parkingService;
    @Mock
    private static InputReaderUtil inputReaderUtil;
    @Mock
    private static ParkingSpotDAO  parkingSpotDAO;
    @Mock
    private static TicketDAO       ticketDAO;

    private        Ticket         ticket;

    @BeforeEach
    private void setUpPerTest() {
        try {
            lenient().when(inputReaderUtil.readVehicleRegistrationNumber()).thenReturn("ABCDEF");

            ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR, false);
            ticket = new Ticket();
            ticket.setInTime(LocalDateTime.now().minusHours(1));
            ticket.setParkingSpot(parkingSpot);
            ticket.setVehicleRegNumber("ABCDEF");
            lenient().when(ticketDAO.getTicket("ABCDEF")).thenReturn(ticket);
            lenient().when(ticketDAO.updateTicket(any(Ticket.class))).thenReturn(true);

            lenient().when(parkingSpotDAO.updateParking(any(ParkingSpot.class))).thenReturn(true);

            parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to set up test mock objects");
        }
    }

    @Test
    public void processExitingVehicleTest() {
        parkingService.processExitingVehicle();
        verify(parkingSpotDAO, Mockito.times(1)).updateParking(any(ParkingSpot.class));
    }

    @Test
    public void processExitingVehicleWhenTicketNotUpToDate() {
        //GIVEN
        when(ticketDAO.updateTicket(any(Ticket.class))).thenReturn(false);
        //WHEN
        parkingService.processExitingVehicle();
        //THEN
        verify(parkingSpotDAO, Mockito.times(0)).updateParking(any(ParkingSpot.class));
    }

    @Test
    public void processExitingVehicleWithNoVehicleRegistrationNumber() throws Exception {
        //GIVEN
        when(inputReaderUtil.readVehicleRegistrationNumber()).thenReturn(null);
        //WHEN
        parkingService.processExitingVehicle();
        //THEN
        assertThat(ticket.getOutTime()).isNull();
    }

    @Test
    public void checkIfRecurringUserIsDetectedWhenUserIsRecurring() throws Exception {
        //GIVEN: a recurring vehicle entering the parking
        ticket.setOutTime(LocalDateTime.now());

        Ticket ticket1 = new Ticket();
        ticket1.setVehicleRegNumber("ABCDEF");
        //WHEN:
        parkingService.checkIfRecurring(ticket1);
        //THEN: isRecurring must be true
        assertThat(ticket1.isRecurring()).isTrue();
    }

    @Test
    public void checkIfRecurringUserIsDetectedWhenUserIsNotRecurring() {
        //GIVEN: a vehicle unknown to the DB enters the parking
        Ticket ticket = new Ticket();
        ticket.setVehicleRegNumber("GHIJKL");
        when(ticketDAO.getTicket("GHIJKL")).thenReturn(null);
        //WHEN: checking if the incoming vehicle is recurring
        parkingService.checkIfRecurring(ticket);
        //THEN: isRecurring must be false
        assertThat(ticket.isRecurring()).isFalse();
    }


    @Test
    public void getNextParkingNumberIfAvailableWhenAvailableForCar() {
        //GIVEN
        when(inputReaderUtil.readSelection()).thenReturn(1);
        when(parkingSpotDAO.getNextAvailableSlot(any(ParkingType.class))).thenReturn(2);
        //WHEN
        ParkingSpot parkingSpot = parkingService.getNextParkingNumberIfAvailable();
        //THEN
        assertThat(parkingSpot.getParkingType()).isEqualTo(ParkingType.CAR);
        assertThat(parkingSpot.isAvailable()).isTrue();
        assertThat(parkingSpot.getId()).isEqualTo(2);

    }

    @Test
    public void getNextParkingNumberIfAvailableWhenAvailableForBike() {
        //GIVEN
        when(inputReaderUtil.readSelection()).thenReturn(2);
        when(parkingSpotDAO.getNextAvailableSlot(any(ParkingType.class))).thenReturn(4);
        //WHEN
        ParkingSpot parkingSpot = parkingService.getNextParkingNumberIfAvailable();
        //THEN
        assertThat(parkingSpot.getParkingType()).isEqualTo(ParkingType.BIKE);
        assertThat(parkingSpot.isAvailable()).isTrue();
        assertThat(parkingSpot.getId()).isEqualTo(4);

    }

    @Test
    public void getNextParkingNumberIfAvailableWhenNotAvailable() {
        //GIVEN
        when(inputReaderUtil.readSelection()).thenReturn(2);
        when(parkingSpotDAO.getNextAvailableSlot(any(ParkingType.class))).thenReturn(0);
        //WHEN
        //assertThrows(Exception.class, () -> parkingService.getNextParkingNumberIfAvailable());
        //THEN
        assertThat(parkingService.getNextParkingNumberIfAvailable()).isNull(); // check that ParkingSpot is null
    }

    @Test
    public void getNextParkingNumberIfAvailableWithWrongVehicleType() {
        //GIVEN
        when(inputReaderUtil.readSelection()).thenReturn(100);
        //WHEN
        assertThat(parkingService.getNextParkingNumberIfAvailable()).isNull();
        //THEN
    }


    @Test
    public void processIncomingCarTest() throws Exception {
        //GIVEN: a car enters the parking
        when(inputReaderUtil.readSelection()).thenReturn(1);
        when(parkingSpotDAO.getNextAvailableSlot(any(ParkingType.class))).thenReturn(2);
        when(inputReaderUtil.readVehicleRegistrationNumber()).thenReturn("ABCDEF");
        //WHEN: processing this car
        parkingService.processIncomingVehicle();
        //THEN: There must be one call to ParkingSpotDAO and one call to TicketDAO
        verify(parkingSpotDAO, Mockito.times(1)).updateParking(any(ParkingSpot.class));
        verify(ticketDAO, Mockito.times(1)).saveTicket(any(Ticket.class));
    }

    @Test
    void processIncomingBikeWithExceptionThrown() throws Exception {
        //GIVEN a bike with no registration number incoming the parking
        when(inputReaderUtil.readSelection()).thenReturn(2);
        when(parkingSpotDAO.getNextAvailableSlot(any(ParkingType.class))).thenReturn(4);
        when(inputReaderUtil.readVehicleRegistrationNumber()).thenThrow(Exception.class);
        //WHEN

        //THEN
        assertThatExceptionOfType(Exception.class).isThrownBy(() -> parkingService.processIncomingVehicle());
    }

}
