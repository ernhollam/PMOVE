package com.parkit.parkingsystem.service;

import com.parkit.parkingsystem.constants.Fare;
import com.parkit.parkingsystem.model.Ticket;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.time.LocalDateTime;

/**
 * <b> Service to calculate fare for exiting vehicle from parking</b>
 * <p>
 * Uses inTime and outTime from ticket to compute the fee.
 * Checks as well if the vehicle is a recurring one and applies a discount to the normal fee.
 * </p>
 *
 * @author Ernholla MARINASY
 * <br><br>
 * @see com.parkit.parkingsystem.App
 * @see com.parkit.parkingsystem.util.InputReaderUtil
 * @see com.parkit.parkingsystem.service.ParkingService
 */
public class FareCalculatorService {

    /**
     * Calculates fare for exiting vehicle.
     * <br><br>
     *
     * @param ticket Ticket with all information needed to calculate a fare
     */
    public void calculateFare(Ticket ticket) {
        final double hourInSecond              = 3600.0;
        final double maxDurationForFreeParking = 0.5;

        if ((ticket.getOutTime() == null) || (ticket.getOutTime().isBefore(ticket.getInTime()))) {
            throw new IllegalArgumentException("Out time provided is incorrect:" + ticket.getOutTime().toString());
        }

        LocalDateTime inTime  = ticket.getInTime();
        LocalDateTime outTime = ticket.getOutTime();

        Duration duration     = Duration.between(inTime, outTime);
        double   durationHour = duration.getSeconds() / hourInSecond;

        if (durationHour <= maxDurationForFreeParking) { //Check if duration is less than 30 min
            ticket.setPrice(BigDecimal.ZERO);
        } else {
            double tmpPrice;
            switch (ticket.getParkingSpot().getParkingType()) {
                case CAR:
                    tmpPrice = durationHour * Fare.CAR_RATE_PER_HOUR;
                    break;
                case BIKE:
                    tmpPrice = durationHour * Fare.BIKE_RATE_PER_HOUR;
                    break;
                default:
                    throw new IllegalArgumentException("Unknown Parking Type");
            }

            if (ticket.isRecurring()) {
                tmpPrice = tmpPrice * (1 - Fare.RECURRING_USER_DISCOUNT);
            }

            BigDecimal price = new BigDecimal(Double.toString(tmpPrice)).setScale(Fare.SCALE, RoundingMode.HALF_UP);
            ticket.setPrice(price);
        }
    }
}
