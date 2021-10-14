package esi.securiteserver;

import esi.security.common.ClientMessage;
import esi.security.common.HandlerSecurity;
import esi.security.common.ServerMessage;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SignatureException;
import java.security.SignedObject;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SealedObject;
import javax.crypto.SecretKey;

/**
 * Class that manage the communication with the client
 *
 * @author Nguyen Khanh-Michel
 */
public final class NetworkServer {

    private final Socket socket;
    private final ObjectInputStream in;
    private final ObjectOutputStream out;
    private final PublicKey clientPubKey;
    private final PublicKey serverPubKey;
    private final PrivateKey serverprivKey;
    private final SecretKey secretKey;

    public NetworkServer(Socket localSocket) throws
            IOException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException {

        this.socket = localSocket;
        out = new ObjectOutputStream(this.socket.getOutputStream());
        out.flush();
        in = new ObjectInputStream(this.socket.getInputStream());
        serverPubKey = HandlerSecurity.getServerPubKey("serverPubKey");
        serverprivKey = HandlerSecurity.getServerPrivKey("serverPrivKey");
        secretKey = getSecretKey();
        clientPubKey = getClientPubKey();

    }

    /**
     * Read and send the private key.
     *
     * @return
     */
    private SecretKey getSecretKey() {
        SecretKey sck = null;
        try {
            SealedObject seobj = (SealedObject) in.readObject();
            sck = (SecretKey) seobj.getObject(serverprivKey);
            System.out.println("secret key at the server");
            System.out.println(HandlerSecurity.bytesToHex(sck.getEncoded()));
        } catch (IOException | ClassNotFoundException | InvalidKeyException | NoSuchAlgorithmException ex) {
            Logger.getLogger(NetworkServer.class.getName()).log(Level.SEVERE, null, ex);
        }
        return sck;
    }

    /**
     * Read and send the client public key
     *
     * @return PublicKey
     */
    private PublicKey getClientPubKey() {
        PublicKey pubkey = null;
        try {
            SealedObject seobj = (SealedObject) in.readObject();
            pubkey = (PublicKey) seobj.getObject(secretKey);
            System.out.println("client's public key at the server");
            System.out.println(HandlerSecurity.bytesToHex(pubkey.getEncoded()));
        } catch (Exception ex) {

            Logger.getLogger(NetworkServer.class.getName()).log(Level.SEVERE, null, ex);
        }
        return pubkey;
    }

    /**
     * This method allows to read a message from the client otherwise block
     * while a client send data.
     *
     * @return a message from client
     */
    public final ClientMessage readFromClient() {

        ClientMessage msgcl = null;

        try {
            SealedObject seobj = (SealedObject) in.readObject();
            SignedObject siobj = (SignedObject) seobj.getObject(secretKey);
            boolean ok = HandlerSecurity.verifySignedObject(siobj, clientPubKey);
            if (!ok) {
                disconnect();
                return null;
            }
            msgcl = (ClientMessage) siobj.getObject();
            System.out.println("msgclient: " + msgcl);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return msgcl;
    }

    /**
     * This method allows to write and send a message to the client.
     *
     * @param srvMsg the server Message.
     */
    public void writeToClient(ServerMessage srvMsg) {

        try {
            try {

                SignedObject siobj = HandlerSecurity.getSignedMsg(srvMsg, serverprivKey);
                SealedObject sealObj = HandlerSecurity.getCipheredObject(siobj, secretKey);
                out.writeObject(sealObj);
                out.flush();
            } catch (NoSuchAlgorithmException | InvalidKeyException | SignatureException | NoSuchPaddingException | IllegalBlockSizeException ex) {
                Logger.getLogger(NetworkServer.class.getName()).log(Level.SEVERE, null, ex);
            }

        } catch (IOException ex) {

        }
    }

    /**
     * Disconect a client
     */
    public void disconnect() {
        try {
            this.socket.close();
        } catch (IOException ex) {
            Logger.getLogger(NetworkServer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
