package esi.security.common;

import java.io.Serializable;

/**
 *
 * @author Nguyen Khanh-Michel
 */
public class ServerMessage implements Serializable {

    private final ServerMessage_Type type;
    private final String data;
    private final String nameOfFile;
    private static final long serialVersionUID = 22L;

    public ServerMessage(ServerMessage_Type type, String data, String nameOfFile) {
        this.type = type;
        this.data = data;
        this.nameOfFile = nameOfFile;
    }

    public ServerMessage_Type getType() {
        return type;
    }

    public String getData() {
        return data;
    }

    public String getNameOfFile() {
        return nameOfFile;
    }

    @Override
    public String toString() {
        return "ServerMessage{" + "type=" + type + ", nameOfFile=" + nameOfFile + '}';
    }

}
