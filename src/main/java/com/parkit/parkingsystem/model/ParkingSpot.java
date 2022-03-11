package com.parkit.parkingsystem.model;

import com.parkit.parkingsystem.constants.ParkingType;

/**
 * Model for parking spot.
 * <p>
 * This contains information about the parking spot such as: the type of vehicle it can take, parking spot ID from DB and its availability.
 * </p>
 */
public class ParkingSpot {
    private int         number;
    private ParkingType parkingType;
    private boolean     isAvailable;

    /**
     * Parking spot constructor.
     *
     * @param number      Parking spot ID
     * @param parkingType Type of vehicle which can be parked
     * @param isAvailable Parking spot availability
     */
    public ParkingSpot(int number, ParkingType parkingType, boolean isAvailable) {
        this.number = number;
        this.parkingType = parkingType;
        this.isAvailable = isAvailable;
    }

    /**
     * Gets parking spot ID.
     *
     * @return Parking spot ID
     */
    public int getId() {
        return number;
    }

    /**
     * Sets parking spot ID.
     *
     * @param number Parking spot ID
     */
    public void setId(int number) {
        this.number = number;
    }

    /**
     * Gets parking spot type.
     *
     * @return Parking spot type
     */
    public ParkingType getParkingType() {
        return parkingType;
    }

    /**
     * Sets parking spot type.
     *
     * @param parkingType Parking spot type
     */
    public void setParkingType(ParkingType parkingType) {
        this.parkingType = parkingType;
    }

    /**
     * Gets parking spot's availability.
     *
     * @return Parking spots availability
     */
    public boolean isAvailable() {
        return isAvailable;
    }

    /**
     * Sets parking spot's availability.
     *
     * @param available Parking spot's availability
     */
    public void setAvailable(boolean available) {
        isAvailable = available;
    }

    /**
     * Checks for equality between two parking spots.
     * @param o Parking spot
     * @return True or false
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ParkingSpot that = (ParkingSpot) o;
        return number == that.number;
    }

    /**
     * Hash code for parking spot.
     * @return Parking spot ID
     */
    @Override
    public int hashCode() {
        return number;
    }
}
