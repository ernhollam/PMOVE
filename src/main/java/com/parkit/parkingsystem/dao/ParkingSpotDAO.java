package com.parkit.parkingsystem.dao;

import com.parkit.parkingsystem.config.DataBaseConfig;
import com.parkit.parkingsystem.constants.DBConstants;
import com.parkit.parkingsystem.constants.ParkingType;
import com.parkit.parkingsystem.model.ParkingSpot;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * <b> Accesses database for parking spot information.</b>
 * <br>
 *
 * @author Tek
 * <br><br>
 * @see com.parkit.parkingsystem.service.InteractiveShell
 * @see com.parkit.parkingsystem.service.ParkingService
 */
public class ParkingSpotDAO {
    private static final Logger logger = LogManager.getLogger("ParkingSpotDAO");

    public DataBaseConfig dataBaseConfig = new DataBaseConfig();

    /**
     * Gets next available parking spot for specified parking type.
     *
     * @param parkingType Parking type, either BIKE or CAR
     *
     * @return ID for next available slot for parking type
     */
    public int getNextAvailableSlot(ParkingType parkingType) {
        Connection con    = null;
        int        result = -1;
        try {
            con = dataBaseConfig.getConnection();
            ResultSet rs = null;
            // Use try-with-resources to clean up java.sql.ResultSet and java.sql.Statement
            try (PreparedStatement ps = con.prepareStatement(DBConstants.GET_NEXT_PARKING_SPOT)) {
                ps.setString(1, parkingType.toString());
                rs = ps.executeQuery();
                if (rs.next()) {
                    result = rs.getInt(1);
                }
            } catch (SQLException sqlException) {
                logger.error("Error while executing query", sqlException);
            } finally {
                dataBaseConfig.closeResultSet(rs);
            }
        } catch (Exception ex) {
            logger.error("Error fetching next available slot", ex);
        } finally {
            dataBaseConfig.closeConnection(con);
        }
        return result;
    }

    /**
     * Updates parking spot availability.
     *
     * @param parkingSpot Parking spot in database
     *
     * @return true if the update was successful, false otherwise
     */
    public boolean updateParking(ParkingSpot parkingSpot) {
        //update the availability fo that parking slot
        Connection con = null;
        try {
            con = dataBaseConfig.getConnection();
            try (PreparedStatement ps = con.prepareStatement(DBConstants.UPDATE_PARKING_SPOT)) {
                ps.setBoolean(1, parkingSpot.isAvailable());
                ps.setInt(2, parkingSpot.getId());
                int updateRowCount = ps.executeUpdate();
                return (updateRowCount == 1);
            }
        } catch (Exception ex) {
            logger.error("Error updating parking info", ex);
            return false;
        } finally {
            dataBaseConfig.closeConnection(con);
        }
    }

}
