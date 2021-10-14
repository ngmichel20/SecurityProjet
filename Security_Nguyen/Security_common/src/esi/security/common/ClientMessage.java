package esi.security.common;

import java.io.Serializable;

/**
 * 
 * @author Nguyen Khanh-Michel
 */
public class ClientMessage implements Serializable {

    private final ClientMessage_Type type;
    private final String data;
    private final String Filename;
    private static final long serialVersionUID = 1L;

    public ClientMessage(ClientMessage_Type type, String data, String Filesname) {
        this.type = type;
        this.data = data;
        this.Filename = Filesname;
    }

    public ClientMessage_Type getType() {
        return type;
    }

    public String getData() {
        return data;
    }

    public String getName() {
        return Filename;
    }

    @Override
    public String toString() {
        return "ClientMessage{" + "type=" + type + ", Filename=" + Filename + '}';
    }

}
