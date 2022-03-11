package com.parkit.parkingsystem.dao;

import com.parkit.parkingsystem.config.DataBaseConfig;
import com.parkit.parkingsystem.constants.DBConstants;
import com.parkit.parkingsystem.constants.ParkingType;
import com.parkit.parkingsystem.model.ParkingSpot;
import com.parkit.parkingsystem.model.Ticket;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.*;

/**
 * <b> Accesses database for ticket information.</b>
 * <br>
 *
 * @author Tek
 * <br><br>
 * @see com.parkit.parkingsystem.service.InteractiveShell
 * @see com.parkit.parkingsystem.service.ParkingService
 */
public class TicketDAO {

    private static final Logger logger = LogManager.getLogger("TicketDAO");

    public DataBaseConfig dataBaseConfig = new DataBaseConfig();

    /**
     * Saves ticket into database.
     *
     * @param ticket Ticket to save
     */
    public void saveTicket(Ticket ticket) {
        Connection con = null;
        try {
            con = dataBaseConfig.getConnection();
            try (PreparedStatement ps = con.prepareStatement(DBConstants.SAVE_TICKET)) {
                //ID, PARKING_NUMBER, VEHICLE_REG_NUMBER, PRICE, IN_TIME, OUT_TIME)
                ps.setInt(1, ticket.getParkingSpot().getId());
                ps.setString(2, ticket.getVehicleRegNumber());
                ps.setBigDecimal(3, ticket.getPrice());
                ps.setTimestamp(4, Timestamp.valueOf(ticket.getInTime()));
                ps.setTimestamp(5, (ticket.getOutTime() == null) ? null : Timestamp.valueOf(ticket.getOutTime()));
                ps.execute();
            } catch (SQLException sqlException) {
                logger.error("Error while preparing statement", sqlException);
            }
        } catch (Exception ex) {
            logger.error("Error fetching next available slot", ex);
        } finally {
            dataBaseConfig.closeConnection(con);
        }
    }

    /**
     * Gets ticket from database with the specified registration number.
     *
     * @param vehicleRegNumber Vehicle registration number
     *
     * @return A ticket with the specified registration number
     */
    public Ticket getTicket(String vehicleRegNumber) {
        Connection con    = null;
        Ticket     ticket = null;
        try {
            con = dataBaseConfig.getConnection();
            ResultSet rs = null;
            try (PreparedStatement ps = con.prepareStatement(DBConstants.GET_TICKET)) {
                //ID, PARKING_NUMBER, VEHICLE_REG_NUMBER, PRICE, IN_TIME, OUT_TIME)
                ps.setString(1, vehicleRegNumber);
                rs = ps.executeQuery();
                if (rs.next()) {
                    ticket = new Ticket();
                    ParkingSpot parkingSpot = new ParkingSpot(rs.getInt(1), ParkingType.valueOf(rs.getString(6)), false);
                    ticket.setParkingSpot(parkingSpot);
                    ticket.setId(rs.getInt(2));
                    ticket.setVehicleRegNumber(vehicleRegNumber);
                    ticket.setPrice(rs.getBigDecimal(3));
                    ticket.setInTime(rs.getTimestamp(4).toLocalDateTime());
                    final Timestamp ticketOutTimeFromDB = rs.getTimestamp(5);
                    if (ticketOutTimeFromDB != null) {
                        //Exit time could be null if the vehicle is still in the parking
                        ticket.setOutTime(ticketOutTimeFromDB.toLocalDateTime());
                    }
                }
                return ticket;
            } catch (SQLException sqlException) {
                logger.error("Error while accessing database to find existing ticket", sqlException);
            } finally {
                dataBaseConfig.closeResultSet(rs);
            }
        } catch (Exception ex) {
            logger.error("Error fetching next available slot", ex);
        } finally {
            dataBaseConfig.closeConnection(con);
        }
        return ticket;
    }

    /**
     * Updates ticket in database and return true if the operation is successful.
     *
     * @param ticket Ticket to update
     *
     * @return True or false
     */
    public boolean updateTicket(Ticket ticket) {
        Connection con = null;
        try {
            con = dataBaseConfig.getConnection();
            try (PreparedStatement ps = con.prepareStatement(DBConstants.UPDATE_TICKET)) {
                ps.setBigDecimal(1, ticket.getPrice());
                ps.setTimestamp(2, Timestamp.valueOf(ticket.getOutTime()));
                ps.setInt(3, ticket.getId());
                ps.execute();
                return true;
            } catch (SQLException sqlException) {
                logger.error("Error while accessing data base to save ticket info", sqlException);
            }
        } catch (Exception ex) {
            logger.error("Error saving ticket info", ex);
        } finally {
            dataBaseConfig.closeConnection(con);
        }
        return false;
    }
}
