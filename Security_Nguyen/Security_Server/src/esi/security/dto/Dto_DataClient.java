package esi.security.dto;

/**
 *
 * @author Nguyen Khanh-Michel
 */
public class Dto_DataClient {

    private final String clientName;
    private final String fileName;

    public Dto_DataClient(String clientName, String fileName) {
        this.clientName = clientName;
        this.fileName = fileName;
    }

    public String getClientName() {
        return clientName;
    }

    public String getFileName() {
        return fileName;
    }

}
