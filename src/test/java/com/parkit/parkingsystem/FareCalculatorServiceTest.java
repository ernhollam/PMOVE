package com.parkit.parkingsystem;

import com.parkit.parkingsystem.constants.Fare;
import com.parkit.parkingsystem.constants.ParkingType;
import com.parkit.parkingsystem.model.ParkingSpot;
import com.parkit.parkingsystem.model.Ticket;
import com.parkit.parkingsystem.service.FareCalculatorService;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class FareCalculatorServiceTest {

    private static FareCalculatorService fareCalculatorService;
    private        Ticket                ticket;
    private        LocalDateTime         outTime;
    private        LocalDateTime         inTime;

    @BeforeAll
    private static void setUp() {
        fareCalculatorService = new FareCalculatorService();
    }

    @BeforeEach
    private void setUpPerTest() {
        ticket  = new Ticket();
        outTime = LocalDateTime.now();
        inTime  = outTime.minusHours(1);
    }

    @Test
    public void calculateFareCar() {
        //GIVEN a car who parked for one hour
        ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR, false);
        ticket.setInTime(inTime);
        ticket.setOutTime(outTime);
        ticket.setParkingSpot(parkingSpot);
        // WHEN calculating the fare
        fareCalculatorService.calculateFare(ticket);
        // THEN the fare must be 1*CAR_RATE_PER_HOUR
        assertThat(ticket.getPrice()).isEqualTo(BigDecimal.valueOf(Fare.CAR_RATE_PER_HOUR).setScale(Fare.SCALE, RoundingMode.HALF_UP));
    }

    @Test
    public void calculateFareBike() {
        // GIVEN a bike which parked for 1 hour
        ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.BIKE, false);
        ticket.setInTime(inTime);
        ticket.setOutTime(outTime);
        ticket.setParkingSpot(parkingSpot);
        // WHEN calculating the fare
        fareCalculatorService.calculateFare(ticket);
        // THEN the fare must be equal to 1*BIKE_RATE_PER_HOUR
        assertThat(ticket.getPrice()).isEqualTo(BigDecimal.valueOf(Fare.BIKE_RATE_PER_HOUR).setScale(Fare.SCALE, RoundingMode.HALF_UP));
    }

    @Test
    public void calculateFareWithoutType() {
        // GIVEN a vehicle with unknown ParkingType
        ParkingSpot parkingSpot = new ParkingSpot(1, null, false);
        ticket.setInTime(inTime);
        ticket.setOutTime(outTime);
        ticket.setParkingSpot(parkingSpot);
        // THEN the fare can not be computed for this vehicle
        assertThrows(NullPointerException.class, () -> fareCalculatorService.calculateFare(ticket));
    }

    @Test
    public void calculateFareWithUnknownType() {
        // GIVEN a vehicle with a ParkingType which is not handled by the app
        ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.TRUCK, false);
        ticket.setInTime(inTime);
        ticket.setOutTime(outTime);
        ticket.setParkingSpot(parkingSpot);
        // THEN the application must throw an IllegalArgumentException
        assertThrows(IllegalArgumentException.class, () -> fareCalculatorService.calculateFare(ticket));
    }

    @Test
    public void calculateFareBikeWithFutureInTime() {
        // GIVEN an outTime is before and inTime
        ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.BIKE, false);
        ticket.setInTime(outTime);
        ticket.setOutTime(inTime);
        ticket.setParkingSpot(parkingSpot);
        // THEN the application must throw an IllegalArgumentException when trying to compute the fare
        assertThrows(IllegalArgumentException.class, () -> fareCalculatorService.calculateFare(ticket));
    }

    @Test
    public void calculateFareCarWithoutOutTime() {
        // GIVEN a car who parked in the parking and has not exited yet
        ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.BIKE, false);
        ticket.setInTime(inTime);
        ticket.setParkingSpot(parkingSpot);
        // THEN the application must throw a NPE when trying to calculate the fare
        assertThrows(NullPointerException.class, () -> fareCalculatorService.calculateFare(ticket));
    }

    @Test
    public void calculateFareBikeWithLessThanThirtyMinutesParkingTime() {
        // GIVEN a bike who parked for 30 minutes or less
        LocalDateTime inTime      = outTime.minusMinutes(30);
        ParkingSpot   parkingSpot = new ParkingSpot(1, ParkingType.BIKE, false);
        ticket.setInTime(inTime);
        ticket.setOutTime(outTime);
        ticket.setParkingSpot(parkingSpot);
        // WHEN calculating the fare
        fareCalculatorService.calculateFare(ticket);
        // THEN the fare must be equal  to 0
        assertThat(ticket.getPrice()).isEqualTo(BigDecimal.ZERO);
    }

    @Test
    public void calculateFareCarWithLessThanThirtyMinutesParkingTime() {
        // GIVEN a car who parked for 30 minutes or less
        LocalDateTime inTime      = outTime.minusMinutes(30);
        ParkingSpot   parkingSpot = new ParkingSpot(1, ParkingType.CAR, false);
        ticket.setInTime(inTime);
        ticket.setOutTime(outTime);
        ticket.setParkingSpot(parkingSpot);
        // WHEN calculating the fare
        fareCalculatorService.calculateFare(ticket);
        // THEN the fare must be equal  to 0
        assertThat(ticket.getPrice()).isEqualTo(BigDecimal.ZERO);
    }

    @Test
    public void calculateFareBikeWithLessThanOneHourParkingTime() {
        // GIVEN a bike which parked for 45 minutes
        LocalDateTime inTime      = outTime.minusMinutes(45);
        ParkingSpot   parkingSpot = new ParkingSpot(1, ParkingType.BIKE, false);
        ticket.setInTime(inTime);
        ticket.setOutTime(outTime);
        ticket.setParkingSpot(parkingSpot);
        // WHEN
        fareCalculatorService.calculateFare(ticket);
        // THEN the fare must be equal to 75% of BIKE_RATE_PER_HOUR
        assertThat(ticket.getPrice()).isEqualTo(BigDecimal.valueOf(0.75 * Fare.BIKE_RATE_PER_HOUR).setScale(Fare.SCALE, RoundingMode.HALF_UP));
    }

    @Test
    public void calculateFareCarWithLessThanOneHourParkingTime() {
        // GIVEN a car which parked for 45 minutes
        LocalDateTime inTime      = outTime.minusMinutes(45);
        ParkingSpot   parkingSpot = new ParkingSpot(1, ParkingType.CAR, false);
        ticket.setInTime(inTime);
        ticket.setOutTime(outTime);
        ticket.setParkingSpot(parkingSpot);
        // WHEN
        fareCalculatorService.calculateFare(ticket);
        // THEN the fare must be equal to 75% of CAR_RATE_PER_HOUR
        assertThat(ticket.getPrice()).isEqualTo(BigDecimal.valueOf(0.75 * Fare.CAR_RATE_PER_HOUR).setScale(Fare.SCALE, RoundingMode.HALF_UP));
    }

    @Test
    public void calculateFareCarWithMoreThanADayParkingTime() {
        // GIVEN a car which parked for more than a day
        LocalDateTime inTime      = outTime.minusHours(24);
        ParkingSpot   parkingSpot = new ParkingSpot(1, ParkingType.CAR, false);
        ticket.setInTime(inTime);
        ticket.setOutTime(outTime);
        ticket.setParkingSpot(parkingSpot);
        // WHEN
        fareCalculatorService.calculateFare(ticket);
        // THEN the fare must be equal to 24*CAR_RATE_PER_HOUR
        assertThat(ticket.getPrice()).isEqualTo(BigDecimal.valueOf(24 * Fare.CAR_RATE_PER_HOUR).setScale(Fare.SCALE, RoundingMode.HALF_UP));
    }

    @Test
    public void checkThatDiscountIsAppliedForCar() {
        // GIVEN a recurring car which parked for 45 minutes
        LocalDateTime inTime      = outTime.minusMinutes(45);
        ParkingSpot   parkingSpot = new ParkingSpot(1, ParkingType.CAR, false);
        ticket.setInTime(inTime);
        ticket.setOutTime(outTime);
        ticket.setParkingSpot(parkingSpot);
        ticket.setRecurring(true);
        // WHEN
        fareCalculatorService.calculateFare(ticket);
        // THEN the discount must be applied
        assertThat(ticket.getPrice()).isEqualTo(BigDecimal.valueOf(0.75 * Fare.CAR_RATE_PER_HOUR * 0.95).setScale(Fare.SCALE, RoundingMode.HALF_UP));
    }

    @Test
    public void checkThatDiscountIsAppliedForBike() {
        // GIVEN a recurring bike which parked for 45 minutes
        LocalDateTime inTime      = outTime.minusMinutes(45);
        ParkingSpot   parkingSpot = new ParkingSpot(1, ParkingType.BIKE, false);
        ticket.setInTime(inTime);
        ticket.setOutTime(outTime);
        ticket.setParkingSpot(parkingSpot);
        ticket.setRecurring(true);
        // WHEN
        fareCalculatorService.calculateFare(ticket);
        // THEN the discount must be applied
        assertThat(ticket.getPrice()).isEqualTo(BigDecimal.valueOf(0.75 * Fare.BIKE_RATE_PER_HOUR * 0.95).setScale(Fare.SCALE, RoundingMode.HALF_UP));
    }

}
