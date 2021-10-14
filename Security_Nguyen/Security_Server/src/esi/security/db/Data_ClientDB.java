package esi.security.db;

import esi.security.dto.Dto_DataClient;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Nguyen Khanh-Michel
 */
public class Data_ClientDB {

    public static void createData_ClientDB() {
        try {
            String qr = "create table data("
                    + "name varchar(20) not null,"
                    + "filename varchar(250) not null,"
                    + "primary key(name,filename),"
                    + "foreign key(name) REFERENCES client(name)"
                    + ")";
            java.sql.Connection connexion = DBManager.getConnection();
            try (Statement stmt = connexion.createStatement()) {
                stmt.executeUpdate(qr);
            }
        } catch (SQLException ex) {
            Logger.getLogger(Data_ClientDB.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static void checkData() throws SQLException {
        String SQL_STATEMENT = "select * from Data";
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

    public static List<Dto_DataClient> getAllFile(String ClientName) {
        List<Dto_DataClient> dt = new ArrayList<>();
        try {
            java.sql.Connection connexion = DBManager.getConnection();
            java.sql.PreparedStatement select;
            String sql = "select * from data where name =?";
            select = connexion.prepareStatement(sql);
            select.setString(1, ClientName);

            ResultSet res = select.executeQuery();
            while (res.next()) {
                Dto_DataClient dto = new Dto_DataClient(res.getString(1), res.getString(2));
                dt.add(dto);
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

        return dt;
    }

    public static void insertDto(Dto_DataClient dto) {
        try {
            java.sql.Connection connexion = DBManager.getConnection();
            java.sql.PreparedStatement insert;
            insert = connexion.prepareStatement(
                    "Insert into data(name, filename)"
                    + "values(?, ?)");
            insert.setString(1, dto.getClientName());
            insert.setString(2, dto.getFileName());
            System.out.println("clientName " + dto.getClientName());
            System.out.println("fileName " + dto.getFileName());
            insert.executeUpdate();
            insert.close();
        } catch (SQLException ex) {
            Logger.getLogger(ClientDB.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static void deletData(String name, String userName) {

        try {

            java.sql.Connection connexion = DBManager.getConnection();
            java.sql.PreparedStatement select;

   
            String sql = "Delete from data where filename = ? and name = ?";
            System.out.println(sql);
            select = connexion.prepareStatement(sql);


            System.out.println("name: " + name);
            System.out.println("userName: " + userName);
            select.setString(1, name);
            select.setString(2, userName);
            select.executeUpdate();

            System.out.println("fin");


        } catch (SQLException ex) {

        }
    }
}
