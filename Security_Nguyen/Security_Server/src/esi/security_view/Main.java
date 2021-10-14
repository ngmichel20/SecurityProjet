package esi.security_view;

import esi.securiteserver.NetworkServer;
import esi.securiteserver.ThreadServer;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.crypto.NoSuchPaddingException;

/**
 *
 * @author Nguyen Khanh-Michel
 */
public class Main {

    public static void main(String[] args) {
        launchServer();

    }

    /**
     * This method allows to start the server and listens to the new
     * connections.
     *
     */
    private static void launchServer() {
        ServerSocket server;
        try {
            server = new ServerSocket(39000);
            System.out.println("---------------server listenning at port 39000----------------");
            while (true) {
                Socket socket = server.accept();
                System.out.println("new client " + socket.toString());
                try {
                    NetworkServer nts = new NetworkServer(socket);
                    new ThreadServer(nts).start();
                } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException ex) {
                    Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
                }
            }

        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

}
