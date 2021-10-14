package esi.securiteserver;

import esi.security.common.ClientMessage;
import esi.security.common.ClientMessage_Type;
import esi.security.common.HandlerSecurity;
import esi.security.common.ServerMessage;
import esi.security.common.ServerMessage_Type;
import esi.security.common.Utils;
import esi.security.db.FacadeDB;
import esi.security.dto.Dto_Client;
import esi.security.dto.Dto_DataClient;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Class that allow to the server to manage several client at the same time (it
 * is a thread).
 *
 * @author Nguyen Khanh-Michel
 */
public class ThreadServer extends Thread {

    private final NetworkServer server;
    private String user;
    private boolean connected;

    private static final String destination = "C:\\Users\\ng-20\\Desktop\\examSECI5"; //it's the path that will store all the users's files/directories.
    private static final String separator = FileSystems.getDefault().getSeparator();

    /**
     * This is the constructor
     * @param server the server 
     */
    public ThreadServer(NetworkServer server) {
        this.server = server;
        connected = false;
    }

    @Override
    public void run() {
        ClientMessage msg = server.readFromClient();
        user = msg.getName();
        try {
            while (msg != null && msg.getType() != ClientMessage_Type.DISCONNECT) {
                switch (msg.getType()) {
                    case REGISTRATION:
                        registration(msg);
                        break;
                    case CONNECT:
                        connect(msg);
                        break;
                    case DOWNLOAD:
                        downloadFile(msg.getName());
                        break;
                    case UPLOAD:
                        uploadFile(msg);
                        break;
                    case DELETE:
                        deleteFile(msg);
                        break;
                    case DELETE_USER:
                        deleteUser(msg);
                        break;
//                        if (false) {
//                            System.out.println("not allow");
//                            break;
//                        }else{
//                            deleteUser(msg);
//                            break;
//                        }
                }

                msg = server.readFromClient();
            }
            server.disconnect();
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }

    }

    private void registration(ClientMessage msg) {
        if (FacadeDB.isClientExist(msg.getName())) {
            server.writeToClient(new ServerMessage(ServerMessage_Type.INVALID, "not registred", msg.getName()));
        } else {
            String sel = Utils.getSalt();
            String hashpwd = HandlerSecurity.getHash(msg.getData(), sel);
            FacadeDB.insertCient(new Dto_Client(msg.getName(), hashpwd, sel));
            server.writeToClient(new ServerMessage(ServerMessage_Type.OK, "registred", msg.getName()));
            System.out.println(" registred");
            try {
                Files.createDirectories(Paths.get(destination + separator + msg.getName()));
            } catch (IOException ex) {
                Logger.getLogger(ThreadServer.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    private void connect(ClientMessage msg) {
        Dto_Client dtocl = FacadeDB.getClientInfos(msg.getName());
        String pwd = msg.getData();
        System.out.println("pwd: " + pwd);
        user = msg.getName();
        boolean exist = FacadeDB.isClientExist(msg.getName());
        System.out.println(" exits: " + exist);
        boolean goodpwd = HandlerSecurity.isgoodPwd(dtocl.getHashPasswd(), pwd, dtocl.getSel());
        System.out.println("good pwd: " + goodpwd);
        connected = exist && goodpwd;
        if (connected) {

            server.writeToClient(new ServerMessage(ServerMessage_Type.OK, "connected", msg.getName()));
            System.out.println(" connected");
        } else {
            server.writeToClient(new ServerMessage(ServerMessage_Type.INVALID, "not connected", msg.getName()));
            server.disconnect();
        }
    }

    private boolean deleteFile(ClientMessage msg) throws IOException {
        boolean deleted = false;

        if (!connected) {
            return deleted;
        }

        String dest = destination + separator + user + separator + msg.getName();
        System.out.println("msg: " + msg.getName());
        System.out.println("dest: " + dest);

        FacadeDB.deleteFileName(msg.getName(), user);

        try {
            Files.delete(Paths.get(dest));
            server.writeToClient(new ServerMessage(ServerMessage_Type.DELETE, "deleted", msg.getName()));
            deleted = true;
        } catch (IOException ex) {
            Logger.getLogger(ThreadServer.class.getName()).log(Level.SEVERE, null, ex);
        }
        System.out.println("The file has been deleted.");
        return deleted;
    }

    private void deleteUser(ClientMessage msg) {
        FacadeDB.deleteClient(msg.getName());
        //delete le contenu du dossier
    }

    private boolean uploadFile(ClientMessage msg) {

        boolean ok = false;
        if (!connected) {
            return ok;
        }
        String dest = destination + separator + user + separator + msg.getName();
        try {
            Files.write(Paths.get(dest), Utils.getByteArrayFromString(msg.getData()));
            ok = true;
        } catch (IOException ex) {

            Logger.getLogger(ThreadServer.class.getName()).log(Level.SEVERE, null, ex);
        }

        FacadeDB.insertFileName(new Dto_DataClient(user, msg.getName()));

        return ok;
    }

    private byte[] getFileData(String name) {
        if (!connected) {
            return null;
        }
        String dest = destination + separator + user + separator + name;
        Path pf = Paths.get(dest);
        if (!Files.exists(pf)) {
            return null;
        }
        byte dt[];
        try {
            dt = Files.readAllBytes(pf);
        } catch (IOException ex) {
            dt = null;
            Logger.getLogger(ThreadServer.class.getName()).log(Level.SEVERE, null, ex);
        }

        return dt;
    }

    private void downloadFile(String name) {
        byte bt[] = getFileData(name);
        if (bt != null) {
            String str = Utils.getStringFromByteArray(bt);
            server.writeToClient(new ServerMessage(ServerMessage_Type.DATA, str, name));
        } else {
            server.writeToClient(new ServerMessage(ServerMessage_Type.INVALID, "file not found", name));
        }

    }

}
