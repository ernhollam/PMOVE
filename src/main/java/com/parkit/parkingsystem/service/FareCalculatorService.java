package com.parkit.parkingsystem.service;

import com.parkit.parkingsystem.constants.Fare;
import com.parkit.parkingsystem.model.Ticket;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class FareCalculatorService {

    public void calculateFare(Ticket ticket){
        if( (ticket.getOutTime() == null) || (ticket.getOutTime().before(ticket.getInTime())) ){
            throw new IllegalArgumentException("Out time provided is incorrect:"+ticket.getOutTime().toString());
        }

        double inHour = ticket.getInTime().getTime(); // in ms
        double outHour = ticket.getOutTime().getTime();

        double durationHour = (outHour - inHour)/3600000;// from milliseconds to hours

        if (durationHour<=0.5) {//Check if duration is less than 30 min
            ticket.setPrice(BigDecimal.ZERO);
        }
        else{
            double tmpPrice;
            switch (ticket.getParkingSpot().getParkingType()) {
                case CAR: {
                    tmpPrice = durationHour * Fare.CAR_RATE_PER_HOUR;
                    break;
                }
                case BIKE: {
                    tmpPrice = durationHour * Fare.BIKE_RATE_PER_HOUR;
                    break;
                }
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