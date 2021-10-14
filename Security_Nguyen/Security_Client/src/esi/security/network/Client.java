package esi.security.network;

import esi.security.common.ClientMessage;
import esi.security.common.ClientMessage_Type;
import esi.security.common.HandlerSecurity;
import esi.security.common.ServerMessage;
import esi.security.common.ServerMessage_Type;
import esi.security.common.Utils;
import java.io.File;
import java.io.IOException;
import java.net.Socket;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;

/**
 * This class that expose certain methods of the model to the view. This class
 * will contains all the methods that the client will use and send the
 * informations to the server and notify it.
 *
 * @author Nguyen Khanh-Michel
 */
public class Client {

    private final NetworkClient networkClient;
    private static Client INSTANCE = null;

    public static synchronized Client getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new Client();
        }

        return INSTANCE;
    }

    /**
     * This is the constructor of Client.
     */
    public Client() {
        this.networkClient = initClient();

    }

    /**
     * This method allows to the client to upload a file.
     *
     * @param fileName the file name path that the user want to upload to the
     * server.
     */
    public void uploadFile(String fileName) {
        Path p = Paths.get(fileName);
        try {
            byte data[] = Files.readAllBytes(p);
            ClientMessage msg = new ClientMessage(ClientMessage_Type.UPLOAD,
                    Utils.getStringFromByteArray(data), p.toFile().getName());
            networkClient.writetoServer(msg);
        } catch (IOException ex) {
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * This method allows to the client to download a file.
     *
     * @param fileName the file that the user want to download from the server.
     * @param destination the destination directory that the user want to put in
     * his computer.
     */
    public void downloadFile(String fileName, String destination) {
        networkClient.writetoServer(new ClientMessage(ClientMessage_Type.DOWNLOAD, fileName, fileName));
        ServerMessage srv = networkClient.readFromServer();
        String sep = FileSystems.getDefault().getSeparator();
        String str = destination + sep + fileName;
        if (srv.getType() == ServerMessage_Type.DATA) {
            byte tab[] = Utils.getByteArrayFromString(srv.getData());
            try {
                Files.write(Paths.get(str), tab);
            } catch (IOException ex) {
                Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else {
            System.out.println(srv.getData());
        }
    }

    /**
     * This method allows to the client to connect to the server.
     *
     * @param passwd the client's password.
     * @param clientName the client's name.
     * @return true if the connection was successfull.
     */
    public boolean connect(String passwd, String clientName) {
        networkClient.writetoServer(new ClientMessage(ClientMessage_Type.CONNECT, passwd, clientName));
        ServerMessage srv = networkClient.readFromServer();
        System.out.println(srv.getData());
        if (srv != null && srv.getType() == ServerMessage_Type.OK) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * This method allows to the client to disconnect from the server.
     */
    public void disconnect() {
        networkClient.disconnect();
    }

    /**
     * This method allows to the client to registre to the server.
     *
     * @param passwd the client's password.
     * @param clientName the client's name.
     * @return true if the registrer was successfull.
     */
    public boolean registration(String passwd, String clientName) {
        networkClient.writetoServer(new ClientMessage(ClientMessage_Type.REGISTRATION, passwd, clientName));
        ServerMessage srv = networkClient.readFromServer();
        System.out.println(srv.getData());
        return srv != null && srv.getType() == ServerMessage_Type.OK;
    }

    /**
     * This method allows to the client to delete a file from the server.
     *
     * @param fileName the file that the client want to delete.
     * @return true if the delete was good.
     */
    public boolean delete(String fileName) {
        ClientMessage msg = new ClientMessage(ClientMessage_Type.DELETE,
                fileName, fileName);
        networkClient.writetoServer(msg);
        ServerMessage srv = networkClient.readFromServer();
        System.out.println(srv.getData());
        return srv != null && srv.getType() == ServerMessage_Type.OK;

    }

    /**
     * This method allows to the admin to delete a user from the server.
     *
     * @param clientNameDirectory the clientNameToDelete.
     */
    public void deleteUser(String clientNameDirectory) {

        Path p = Paths.get(clientNameDirectory);

        try {
            byte data[] = Files.readAllBytes(p);
            ClientMessage msg = new ClientMessage(ClientMessage_Type.DELETE_USER,
                    Utils.getStringFromByteArray(data), p.toFile().getName());
            networkClient.writetoServer(msg);
            //Files.delete(p);

            //deleteDirectory(p);
        } catch (IOException ex) {
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
        }

//        networkClient.writetoServer(new ClientMessage(ClientMessage_Type.DELETE_USER, clientName, clientName));
//        ServerMessage srv = networkClient.readFromServer();
//        System.out.println(srv.getData());
        //return srv != null && srv.getType() == ServerMessage_Type.OK;
    }

    public boolean deleteDirectory(File directoryToBeDeleted) {
        File[] allContents = directoryToBeDeleted.listFiles();
        if (allContents != null) {
            for (File file : allContents) {
                deleteDirectory(file);
            }
        }

        return directoryToBeDeleted.delete();
    }

    /**
     * This method allows to initialize the clienta and generate all the
     * necessary key for an secured communication between him and the server.
     *
     * @return the networkClient.
     */
    private NetworkClient initClient() {
        Socket socket;
        NetworkClient ntcl = null;
        try {
            socket = new Socket("127.0.0.1", 39000);
            System.out.println("Generating secret key");
            SecretKey secretkey = HandlerSecurity.generateSecretKey();
            System.out.println("Generating private and public key");
            KeyPair kpcl = HandlerSecurity.generateKeys();
            PublicKey clientpub = kpcl.getPublic();
            PrivateKey clientpriv = kpcl.getPrivate();
            System.out.println("Sending secret and public key to the server\n\n");
            System.out.println("==========================================================================================\n");
            ntcl = new NetworkClient(socket, secretkey, clientpub, clientpriv);

        } catch (IOException | InvalidKeyException | NoSuchAlgorithmException | NoSuchProviderException | NoSuchPaddingException ex) {
            System.err.println("Cannot etablish a connection.\n");
            System.exit(0);
        }
        return ntcl;

    }
}
