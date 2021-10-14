package esi.security.db;

import esi.security.dto.Dto_Client;
import esi.security.dto.Dto_DataClient;
import java.sql.SQLException;
import java.util.List;

/**
 * This class is used for the DB. The main class will be there just to check the DB.
 * 
 * @author Nguyen Khanh-Michel
 */
public class FacadeDB {

    public static boolean isClientExist(String name) {
        return ClientDB.clientExists(name);
    }

    public static void deleteClient(String name) {
        ClientDB.deleteUser(name);
    }

    public static void insertCient(Dto_Client dto) {
        ClientDB.insertDb(dto);
    }

    public static Dto_Client getClientInfos(String name) {
        return ClientDB.getDto_Client(name);
    }
    
    public static void checkClient() throws SQLException {
        ClientDB.checkClient();
    }

    public List<Dto_DataClient> getAllClient_Files(String name) {
        return Data_ClientDB.getAllFile(name);
    }

    public static void insertFileName(Dto_DataClient dto) {
        Data_ClientDB.insertDto(dto);
    }

    public static void deleteFileName(String name, String userName) {
        Data_ClientDB.deletData(name, userName);
    }
    
    public static void checkData() throws SQLException {
        Data_ClientDB.checkData();
    }

    public static void createDB_end_tables() {
        DBManager.createTables();
    }

    public static void main(String[] args) throws SQLException {
        //FacadeDB.createDB_end_tables();
        //FacadeDB.getClientInfos("ngmichel2");
        FacadeDB.deleteClient("ngmichel");
        FacadeDB.checkClient();
        Data_ClientDB.checkData();
//        deleteFileName("capture.png", "ngmichel2");
//        deleteFileName("capturev2.png", "ngmichel2");
//        deleteFileName("capturev3.png", "ngmichel2");
        Data_ClientDB.checkData();
        
        Data_ClientDB.getAllFile("ngmichel2");
        //deleteFileName("capturev2.png");
    }
}
