/*
 * DBManager.java
 */
package esi.security.db;

import java.sql.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This class offers the tools of connexion and transaction managments.
 *
 * @author Nguyen Khanh-Michel
 */
public class DBManager {

    private static Connection connection;

    /**
     * Get a unique connection that can be used in the session
     *
     * @return a connection
     * @throws IllegalArgumentException
     */
    public static Connection getConnection() throws IllegalArgumentException {
        if (connection == null) {
            try {
                Class.forName("org.apache.derby.jdbc.EmbeddedDriver");
                connection = DriverManager.getConnection("jdbc:derby:DBServer;create=true");
            } catch (SQLException ex) {
                throw new IllegalArgumentException("Connexion impossible: " + ex.getMessage());
            } catch (ClassNotFoundException ex) {
                Logger.getLogger(DBManager.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return connection;
    }

    /**
     * Validate the current transaction
     */
    public static void validateTransaction() {
        try {
            getConnection().commit();
            getConnection().setAutoCommit(true);
        } catch (SQLException ex) {

        }
    }

    /**
     * Cancel the current transaction
     */
    public static void cancelTransaction() {
        try {
            getConnection().rollback();
            getConnection().setAutoCommit(true);
        } catch (SQLException ex) {

        }
    }

    /**
     * Create the Tables
     */
    public static void createTables() {
        ClientDB.createClientDB();
        Data_ClientDB.createData_ClientDB();
        System.out.println(" tables created");
    }

}
