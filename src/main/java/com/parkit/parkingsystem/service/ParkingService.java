package com.parkit.parkingsystem.service;

import com.parkit.parkingsystem.constants.ParkingType;
import com.parkit.parkingsystem.dao.ParkingSpotDAO;
import com.parkit.parkingsystem.dao.TicketDAO;
import com.parkit.parkingsystem.model.ParkingSpot;
import com.parkit.parkingsystem.model.Ticket;
import com.parkit.parkingsystem.util.InputReaderUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * <b> Service to manage incoming or exiting vehicle.</b>
 * <br>
 *
 * @author Ernholla MARINASY
 * <br><br>
 * @see com.parkit.parkingsystem.App
 * @see com.parkit.parkingsystem.util.InputReaderUtil
 * @see com.parkit.parkingsystem.service.FareCalculatorService
 */
public class ParkingService {

    private static final Logger logger = LogManager.getLogger("ParkingService");

    private static final FareCalculatorService fareCalculatorService = new FareCalculatorService();

    private final InputReaderUtil inputReaderUtil;
    private final ParkingSpotDAO  parkingSpotDAO;
    private final TicketDAO       ticketDAO;

    /**
     * Constructor for ParkingService.
     *
     * @param inputReaderUtil Util to read input from interactive shell
     * @param parkingSpotDAO  Get data from DB about the parking spots
     * @param ticketDAO       Get data from DB about saved tickets
     */
    public ParkingService(InputReaderUtil inputReaderUtil, ParkingSpotDAO parkingSpotDAO, TicketDAO ticketDAO) {
        this.inputReaderUtil = inputReaderUtil;
        this.parkingSpotDAO = parkingSpotDAO;
        this.ticketDAO = ticketDAO;
    }

    /**
     * Process incoming vehicle.
     * <p>
     * At the entrance of a new vehicle, it is first checked whether the parking is full or not for the specified type of vehicle. If there are still available slots, the entrance time as well as the
     * vehicle's registration number are saved in the ticket. It is also checked if the vehicle is a recurring one, if so, a boolean is set to true.
     * </p>
     * <br><br>
     *
     * @throws Exception that might be thrown when reading vehicle registration number from interactive shell
     */
    public void processIncomingVehicle() throws Exception {
        try {
            ParkingSpot parkingSpot = getNextParkingNumberIfAvailable();
            // parkingSpot.getId() could never be <=0 due to the fact that if parkingSpot is not null, it would mean the parkingNumber is never reassigned
            if (parkingSpot != null) {
                String vehicleRegNumber = getVehicleRegNumber();
                parkingSpot.setAvailable(false);
                parkingSpotDAO.updateParking(parkingSpot); //allot this parking space and mark its availability as false

                LocalDateTime inTime = LocalDateTime.now();
                Ticket        ticket = new Ticket();
                //ID, PARKING_NUMBER, VEHICLE_REG_NUMBER, PRICE, IN_TIME, OUT_TIME)
                //ticket.setId(ticketID);
                ticket.setParkingSpot(parkingSpot);
                ticket.setVehicleRegNumber(vehicleRegNumber);
                if (checkIfRecurring(ticket)) {
                    System.out.println("Welcome back! As a recurring user of our parking lot, you'll benefit from a 5% discount");
                } //Check if this vehicle is a recurring user
                ticket.setPrice(BigDecimal.valueOf(0));
                ticket.setInTime(inTime);
                ticket.setOutTime(null);
                ticketDAO.saveTicket(ticket);
                System.out.println("Generated Ticket and saved in DB");
                System.out.println("Please park your vehicle in spot number:" + parkingSpot.getId());
                System.out.println("Recorded in-time for vehicle number:" + vehicleRegNumber + " is:" + inTime);
            }
        } catch (Exception e) {
            logger.error("Unable to process incoming vehicle", e);
            throw e;
        }
    }

    private String getVehicleRegNumber() throws Exception {
        System.out.println("Please type the vehicle registration number and press enter key");
        return inputReaderUtil.readVehicleRegistrationNumber();
    }

    /**
     * Gets the new available parking number.
     *
     * @return ParkingSpot Next available parking spot for specified vehicle type
     */
    public ParkingSpot getNextParkingNumberIfAvailable() {
        int         parkingNumber;
        ParkingSpot parkingSpot = null;
        try {
            ParkingType parkingType = getVehicleType();
            parkingNumber = parkingSpotDAO.getNextAvailableSlot(parkingType);
            if (parkingNumber > 0) {
                parkingSpot = new ParkingSpot(parkingNumber, parkingType, true);
            } else {
                throw new Exception("Error fetching parking number from DB. Parking slots might be full");
            }
        } catch (IllegalArgumentException ie) {
            logger.error("Error parsing user input for type of vehicle", ie);
        } catch (Exception e) {
            logger.error("Error fetching next available parking slot", e);
        }
        return parkingSpot;
    }

    private ParkingType getVehicleType() {
        System.out.println("Please select vehicle type from menu");
        System.out.println("1 CAR");
        System.out.println("2 BIKE");
        int input = inputReaderUtil.readSelection();
        switch (input) {
            case 1:
                return ParkingType.CAR;
            case 2:
                return ParkingType.BIKE;
            default:
                System.out.println("Incorrect input provided");
                throw new IllegalArgumentException("Entered input is invalid");
        }
    }

    /**
     * Process exiting vehicle.
     * <p>
     * At the exit of a vehicle, the exiting time is saved into the ticket. This exiting time is used to compute the fare. If the vehicle is a recurring one, a discount is applied.
     * </p>
     */
    public void processExitingVehicle() {
        try {
            String        vehicleRegNumber = getVehicleRegNumber();
            Ticket        ticket           = ticketDAO.getTicket(vehicleRegNumber);
            LocalDateTime outTime          = LocalDateTime.now();
            ticket.setOutTime(outTime);
            fareCalculatorService.calculateFare(ticket);
            if (ticketDAO.updateTicket(ticket)) {
                ParkingSpot parkingSpot = ticket.getParkingSpot();
                parkingSpot.setAvailable(true);
                parkingSpotDAO.updateParking(parkingSpot);
                System.out.println("Please pay the parking fare:" + ticket.getPrice());
                System.out.println("Recorded out-time for vehicle number:" + ticket.getVehicleRegNumber() + " is:" + outTime);
            } else {
                System.out.println("Unable to update ticket information. Error occurred");
            }
        } catch (Exception e) {
            logger.error("Unable to process exiting vehicle", e);
        }
    }

    /**
     * Checks if the vehicle is recurring.
     * <br><br>
     *
     * @param ticket Ticket with information about recurring vehicle or not
     *
     * @return true or false
     */
    public boolean checkIfRecurring(Ticket ticket) {
        if (ticketDAO.getTicket(ticket.getVehicleRegNumber()) != null) {
            // The vehicle must have entered and exited the parking at least once
            ticket.setRecurring(true);
            return true;
        }
        return false;
    }
}
