package com.parkit.parkingsystem;

import com.parkit.parkingsystem.service.InteractiveShell;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**<b>Main application for Park'it system</b>
 *<p>
 * A command line app for managing the parking system.
 * This app uses Java to run and stores the data in Mysql DB.
 *</p>
 * <p>
 * <strong>The application is made of three steps</strong> is made of three steps:
 * <ul>
 * 	<li>At launch of the application, the user must chose either parking or existing a vehicle (or quit the application)</li>
 * 	<li>When the user chooses to park a vehicle, they are asked which kind of vehicle (car or bike), its registration number then the user is prompt in which parking lot to park</li>
 * 	<li>When exiting the parking, the user inputs the registration number. The application calculates and displays the fare according to the parking duration</li>
 *</ul>
 * @author Ernholla MARINASY
 *
 * @see com.parkit.parkingsystem.service.ParkingService
 * @see com.parkit.parkingsystem.service.FareCalculatorService
 */
public class App {
    private static final Logger logger = LogManager.getLogger("App");

    /** Main method for Park'it application.
     <br><br>
     * @param args Input arguments
     * @throws Exception an Exception
     */
    public static void main(String[] args) throws Exception {
        logger.info("Initializing Parking System");
        InteractiveShell.loadInterface();
    }
}
