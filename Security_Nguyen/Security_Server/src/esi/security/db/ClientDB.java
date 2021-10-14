package esi.security.db;

import esi.security.dto.Dto_Client;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This is the DB CLIENT
 *
 * @author Nguyen Khanh-Michel
 */
public class ClientDB {

    /**
     * This method will create the Client DB and has already been executed for
     * the first time to have the DB creation.
     */
    public static void createClientDB() {
        try {
            String client = "CREATE TABLE CLIENT(name varchar(20) not null primary key,"
                    + "hashpassword varchar(5000) not null,"
                    + "sel varchar(15)not null)";

            java.sql.Connection connexion = DBManager.getConnection();
            Statement stmt = connexion.createStatement();
            stmt.executeUpdate(client);
            stmt.close();
        } catch (SQLException ex) {
            Logger.getLogger(ClientDB.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    /**
     * This method allows to get a client.
     *
     * @param name the client's name
     * @return the Dto_Client
     */
    public static Dto_Client getDto_Client(String name) {
        Dto_Client cl = null;
        try {
            java.sql.Connection connexion = DBManager.getConnection();
            java.sql.PreparedStatement select;
            String sql = "select * from client where name =?";
            select = connexion.prepareStatement(sql);
            select.setString(1, name);
            ResultSet res = select.executeQuery();
            if (res.next()) {
                cl = new Dto_Client(res.getString(1), res.getString(2), res.getString(3));
            }

            java.sql.ResultSetMetaData rsmd = res.getMetaData();
            int columnCount = rsmd.getColumnCount();
            for (int x = 1; x <= columnCount; x++) {
                System.out.format("%20s", rsmd.getColumnName(x) + "|");
            }
            System.out.println("");
            while (res.next()) {
                for (int x = 1; x <= columnCount; x++) {
                    System.out.format("%20s", res.getString(x) + "|");
                }
                System.out.println("");
            }
            System.out.println("");

        } catch (SQLException ex) {

        }
        return cl;
    }

    /**
     * This method allows to check all the Client.
     *
     * @throws SQLException
     */
    public static void checkClient() throws SQLException {
        String SQL_STATEMENT = "select * from CLIENT";
        java.sql.Connection connection = DBManager.getConnection();
        Statement stmt = connection.createStatement();
        ResultSet rs = stmt.executeQuery(SQL_STATEMENT);
        java.sql.ResultSetMetaData rsmd = rs.getMetaData();
        int columnCount = rsmd.getColumnCount();
        for (int x = 1; x <= columnCount; x++) {
            System.out.format("%20s", rsmd.getColumnName(x) + "|");
        }
        System.out.println("");
        while (rs.next()) {
            for (int x = 1; x <= columnCount; x++) {
                System.out.format("%20s", rs.getString(x) + "|");
            }
            System.out.println("");
        }
        System.out.println("");
        if (stmt != null) {
            stmt.close();
        }
        if (connection != null) {
            stmt.close();
        }
    }

    /**
     * This method allows to delete a client from the DB.
     *
     * @param dto
     */
    public static void insertDb(Dto_Client dto) {
        try {
            java.sql.Connection connexion = DBManager.getConnection();
            java.sql.PreparedStatement insert;
            insert = connexion.prepareStatement(
                    "Insert into Client(name, hashpassword, sel)"
                    + "values(?, ?, ?)");
            insert.setString(1, dto.getName());
            insert.setString(2, dto.getHashPasswd());
            insert.setString(3, dto.getSel());
            insert.executeUpdate();
            insert.close();
        } catch (SQLException ex) {
            Logger.getLogger(ClientDB.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    /**
     * This method allows to delete an user.
     *
     * @param name the user's name.
     */
    public static void deleteUser(String name) {


        try {


            java.sql.Connection connexion = DBManager.getConnection();
            java.sql.PreparedStatement select;
  
            String sql = "Delete from Client where name=?";// + " and name="+userName;  
            System.out.println(sql);
            select = connexion.prepareStatement(sql);

            System.out.println("name: " + name);
            select.setString(1, name);
            select.executeUpdate();
    
            System.out.println("fin");

        } catch (SQLException ex) {

        }

    }

    /**
     * This method allows to check if a client already exist.
     *
     * @param name the client's name.
     * @return true if the client already exist.
     */
    public static boolean clientExists(String name) {
        ResultSet res = null;
        boolean ok = false;
        try {
            java.sql.Connection connexion = DBManager.getConnection();
            java.sql.PreparedStatement select;
            String sql = "select * from client where name =?";
            select = connexion.prepareStatement(sql);
            select.setString(1, name);
            res = select.executeQuery();
            ok = res.next();
        } catch (SQLException ex) {
            Logger.getLogger(ClientDB.class.getName()).log(Level.SEVERE, null, ex);
        }

        return ok;
    }

}
