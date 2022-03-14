package com.parkit.parkingsystem.constants;

/**
 * Constants for ticket price.
 *
 * @see com.parkit.parkingsystem.service.FareCalculatorService
 */
public class Fare {
    public static final double BIKE_RATE_PER_HOUR = 1.0;
    public static final double CAR_RATE_PER_HOUR  = 1.5;

    public static final double RECURRING_USER_DISCOUNT = 0.05;
    // Parameters for use of big decimal
    public static final int    SCALE                   = 2; // 2 digits after the decimal
}
