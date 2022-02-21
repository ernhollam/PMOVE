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

import java.util.Date;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ParkingServiceTest {

    private static ParkingService parkingService;

    @Mock
    private static InputReaderUtil inputReaderUtil;
    @Mock
    private static ParkingSpotDAO parkingSpotDAO;
    @Mock
    private static TicketDAO ticketDAO;

    @BeforeEach
    private void setUpPerTest() {
        try {
            lenient().when(inputReaderUtil.readVehicleRegistrationNumber()).thenReturn("ABCDEF");

            ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR,false);
            Ticket ticket = new Ticket();
            ticket.setInTime(new Date(System.currentTimeMillis() - (60*60*1000)));
            ticket.setParkingSpot(parkingSpot);
            ticket.setVehicleRegNumber("ABCDEF");
            lenient().when(ticketDAO.getTicket("ABCDEF")).thenReturn(ticket);
            lenient().when(ticketDAO.updateTicket(any(Ticket.class))).thenReturn(true);

            lenient().when(parkingSpotDAO.updateParking(any(ParkingSpot.class))).thenReturn(true);

            parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);
        } catch (Exception e) {
            e.printStackTrace();
            throw  new RuntimeException("Failed to set up test mock objects");
        }
    }

    @Test
    public void processExitingVehicleTest(){
        parkingService.processExitingVehicle();
        verify(parkingSpotDAO, Mockito.times(1)).updateParking(any(ParkingSpot.class));
    }


    @Test
    public void checkIfRecurringUserIsDetectedWhenUserIsRecurring(){
        //GIVEN: a recurring vehicle entering the parking
        Ticket ticket = new Ticket();
        ticket.setVehicleRegNumber("ABCDEF");
        //WHEN:
        parkingService.checkIfRecurring(ticket);
        //THEN: isRecurring must be true
        assertThat(ticket.getRecurring()).isTrue();
    }

    @Test
    public void checkIfRecurringUserIsDetectedWhenUserIsNotRecurring(){
        //GIVEN: a vehicle unknown to the DB enters the parking
        Ticket ticket = new Ticket();
        ticket.setVehicleRegNumber("GHIJKL");
        when(ticketDAO.getTicket("GHIJKL")).thenReturn(null);
        //WHEN: checking if the incoming vehicle is recurring
        parkingService.checkIfRecurring(ticket);
        //THEN: isRecurring must be false
        assertThat(ticket.getRecurring()).isFalse();
    }


    @Test
    public void getNextParkingNumberIfAvailableWhenAvailableForCar(){
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
    public void getNextParkingNumberIfAvailableWhenAvailableForBike(){
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
    public void getNextParkingNumberIfAvailableWhenNotAvailable(){
        //GIVEN
        when(inputReaderUtil.readSelection()).thenReturn(2);
        when(parkingSpotDAO.getNextAvailableSlot(any(ParkingType.class))).thenReturn(0);
        //WHEN
        //assertThrows(Exception.class, () -> parkingService.getNextParkingNumberIfAvailable());
        //THEN
        assertThat(parkingService.getNextParkingNumberIfAvailable()).isNull(); // check that ParkingSpot is null
    }

    @Test
    public void getNextParkingNumberIfAvailableWithWrongVehicleType(){
        //GIVEN
        when(inputReaderUtil.readSelection()).thenReturn(3);
        //WHEN
        //assertThrows(IllegalArgumentException.class, () -> parkingService.getNextParkingNumberIfAvailable());
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
}
