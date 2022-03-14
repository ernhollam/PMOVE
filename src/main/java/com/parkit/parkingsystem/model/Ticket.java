package com.parkit.parkingsystem.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Model for ticket.
 * <p>
 *     The ticket contains information about the vehicle such as:
 *     its type, its registration number, the price the user has to pay, entry and exit time and recurring information
 * </p>
 */
public class Ticket {
    private int           id;
    private ParkingSpot   parkingSpot;
    private String        vehicleRegNumber;
    private BigDecimal    price;
    private LocalDateTime inTime;
    private LocalDateTime outTime;
    private boolean       isRecurring;

    /**
     * Gets information about vehicle recurrence.
     * @return true or false
     */
    public boolean isRecurring() {
        return isRecurring;
    }

    /**
     * Sets recurrence information.
     * @param recurring True or false
     */
    public void setRecurring(boolean recurring) {
        this.isRecurring = recurring;
    }

    /**
     * Gets ticket ID.
     * @return Ticket ID
     */
    public int getId() {
        return id;
    }

    /**
     * Sets ticket ID.
     * @param id Ticket ID
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     *  Gets parking spot from ticket.
     * @return Parking spot
     */
    public ParkingSpot getParkingSpot() {
        return parkingSpot;
    }

    /**
     * Sets parking spot in ticket.
     * @param parkingSpot Parking spot
     */
    public void setParkingSpot(ParkingSpot parkingSpot) {
        this.parkingSpot = parkingSpot;
    }

    /**
     * Gets vehicle registration number.
     * @return Vehicle registration number
     */
    public String getVehicleRegNumber() {
        return vehicleRegNumber;
    }

    /**
     * Sets vehicle registration number.
     * @param vehicleRegNumber Vehicle registration number
     */
    public void setVehicleRegNumber(String vehicleRegNumber) {
        this.vehicleRegNumber = vehicleRegNumber;
    }

    /**
     * Gets price.
     * @return Price to pay when leaving the parking
     */
    public BigDecimal getPrice() {
        return price;
    }

    /**
     * Sets price.
     * @param price Price to pay when leaving the parking
     */
    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    /**
     * Gets entry time.
     * @return Entry time
     */
    public LocalDateTime getInTime() {
        return inTime;
    }

    /**
     * Sets entry time.
     * @param inTime Entry time
     */
    public void setInTime(LocalDateTime inTime) {
        this.inTime = inTime;
    }

    /**
     * Gets exit time.
     * @return Exit time
     */
    public LocalDateTime getOutTime() {
        return outTime;
    }

    /**
     * Sets exit time.
     * @param outTime Exit time
     */
    public void setOutTime(LocalDateTime outTime) {
        this.outTime = outTime;
    }
}
