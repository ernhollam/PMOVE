package com.parkit.parkingsystem.integration.config;

import com.parkit.parkingsystem.config.DataBaseConfig;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.FileInputStream;
import java.sql.*;
import java.util.Properties;

/**
 * Configures connection to test database.
 */
public class DataBaseTestConfig extends DataBaseConfig {

    private static final Logger logger = LogManager.getLogger("DataBaseTestConfig");

    /**
     * Gets connection information to database.
     *
     * @return Connection
     */
    public Connection getConnection() {
        logger.info("Create DB connection");

        Properties dbProperties = new Properties();
        String     filepath     = "resources/config.properties";

        try (FileInputStream fileInputStream = new FileInputStream(filepath)) {
            dbProperties.load(fileInputStream);
            String dbDriver   = dbProperties.getProperty("db.driver");
            String dbURL      = dbProperties.getProperty("db.urlTest");
            String dbUsername = dbProperties.getProperty("db.username");
            String dbPassword = dbProperties.getProperty("db.password");

            Class.forName(dbDriver);

            return DriverManager.getConnection(dbURL, dbUsername, dbPassword);
        } catch (Exception e) {
            logger.error("Error while reading properties file", e);
        }
        return null;
    }

    /**
     * Closes connection to database.
     *
     * @param con Connection to close
     */
    public void closeConnection(Connection con) {
        if (con != null) {
            try {
                con.close();
                logger.info("Closing DB connection");
            } catch (SQLException e) {
                logger.error("Error while closing connection", e);
            }
        }
    }

    /**
     * Closes prepared statement.
     *
     * @param ps Prepared statement to close
     */
    public void closePreparedStatement(PreparedStatement ps) {
        if (ps != null) {
            try {
                ps.close();
                logger.info("Closing Prepared Statement");
            } catch (SQLException e) {
                logger.error("Error while closing prepared statement", e);
            }
        }
    }

    /**
     * Closes result set object.
     *
     * @param rs Result set object to close
     */
    public void closeResultSet(ResultSet rs) {
        if (rs != null) {
            try {
                rs.close();
                logger.info("Closing Result Set");
            } catch (SQLException e) {
                logger.error("Error while closing result set", e);
            }
        }
    }
}
