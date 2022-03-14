package com.parkit.parkingsystem.util;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.nio.charset.StandardCharsets;
import java.util.Scanner;

/**<b> Reads user input from console application.</b>
 *
 * @author Tek
 *<br><br>
 * @see com.parkit.parkingsystem.App
 * @see com.parkit.parkingsystem.service.InteractiveShell
 * @see com.parkit.parkingsystem.service.ParkingService
 *
 */
public class InputReaderUtil {

    private static final Scanner scan   = new Scanner(System.in, StandardCharsets.UTF_8);
    private static final Logger  logger = LogManager.getLogger("InputReaderUtil");

    /**<b>Reads user's choice from the menu selection.</b>
     * <br>
     * <p>
     *     1: new vehicle is incoming<br>
     *     2: exiting a vehicle<br>
     *     3: exit the application
     * </p>
     * @return User's choice from  menu
     * <br><br>
     * @see com.parkit.parkingsystem.service.ParkingService
     */
    public int readSelection() {
        try {
            return Integer.parseInt(scan.nextLine());
        } catch (Exception e) {
            logger.error("Error while reading user input from Shell", e);
            System.out.println("Error reading input. Please enter valid number for proceeding further");
            return -1;
        }
    }

    /**<b>Reads the vehicle's registration number from interactive shell.</b>
     * <br>
     *
     * @return String with the vehicle's registration number
     * @throws java.lang.Exception
     * <br><br>
     * @see com.parkit.parkingsystem.service.ParkingService
     */
    public String readVehicleRegistrationNumber() throws Exception {
        try {
            String vehicleRegNumber = scan.nextLine();
            if (vehicleRegNumber == null || vehicleRegNumber.trim().length() == 0) {
                throw new IllegalArgumentException("Invalid input provided");
            }
            return vehicleRegNumber;
        } catch (Exception e) {
            logger.error("Error while reading user input from Shell", e);
            System.out.println("Error reading input. Please enter a valid string for vehicle registration number");
            throw e;
        }
    }


}
