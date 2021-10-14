package esi.security.network;

import esi.security.common.ClientMessage;
import esi.security.common.ClientMessage_Type;
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
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SealedObject;
import javax.crypto.SecretKey;

/**
 * This class will manage the network and it will allows to have dialog with the
 * server.
 *
 * @author Nguyen Khanh-Michel
 */
public final class NetworkClient {

    private final Socket socket;
    private final ObjectInputStream in;
    private final ObjectOutputStream out;
    private final SecretKey secretkey;
    private final PublicKey clientPubKey;
    private final PublicKey serveurPubKey;
    private final PrivateKey clientPrivKey;

    /**
     * This is the constructor.
     *
     * @param socket the socket that will be send.
     * @param secretkey the sercretKey.
     * @param clientPubKey the client public's key.
     * @param clientPrivKey the client private's key.
     * @throws IOException
     * @throws NoSuchAlgorithmException
     * @throws NoSuchPaddingException
     * @throws InvalidKeyException
     */
    public NetworkClient(Socket socket, SecretKey secretkey, PublicKey clientPubKey, PrivateKey clientPrivKey)
            throws IOException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException {
        this.socket = socket;
        out = new ObjectOutputStream(this.socket.getOutputStream());
        out.flush();
        in = new ObjectInputStream(this.socket.getInputStream());
        this.secretkey = secretkey;
        this.clientPubKey = clientPubKey;
        this.serveurPubKey = getserveurpubKey();
        this.clientPrivKey = clientPrivKey;
        initSession();

    }

    /**
     * This method allows to obtain the public key from the server.
     *
     * @return PublicKey
     */
    public PublicKey getserveurpubKey() {
        return HandlerSecurity.getServerPubKey("serverPubKey");
    }

    /**
     * This method allows to send the secret key to the server.
     */
    private void sendSecretKey() {

        try {
            Cipher ciph = Cipher.getInstance("RSA");
            ciph.init(Cipher.ENCRYPT_MODE, serveurPubKey);
            SealedObject so = new SealedObject(secretkey, ciph);
            out.writeObject(so);
            out.flush();
        } catch (IOException | InvalidKeyException | NoSuchAlgorithmException
                | IllegalBlockSizeException | NoSuchPaddingException ex) {
            Logger.getLogger(NetworkClient.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * This method allows to send the client's public key to the server.
     */
    private void sendPublickey() {
        try {
            Cipher ciph = Cipher.getInstance("AES");
            ciph.init(Cipher.ENCRYPT_MODE, secretkey);
            out.writeObject(new SealedObject(clientPubKey, ciph));
            out.flush();
        } catch (IOException | InvalidKeyException | NoSuchAlgorithmException
                | IllegalBlockSizeException | NoSuchPaddingException ex) {
            Logger.getLogger(NetworkClient.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * This method allows to initialize the dialog and send the secret and
     * public key to the client.
     */
    private void initSession() {
        sendSecretKey();
        sendPublickey();
    }

    /**
     * This method allows to read a message from the server or bloc while the
     * server send datas.
     *
     * @return a ServerMessage
     */
    public ServerMessage readFromServer() {

        ServerMessage msgSer = null;
        boolean verified;
        try {
            SealedObject seobj = (SealedObject) in.readObject();
            SignedObject siobj = HandlerSecurity.getUnCipheredObject(seobj, secretkey);
            verified = HandlerSecurity.verifySignedObject(siobj, serveurPubKey);
            if (!verified) {
                disconnect();
                System.exit(0x12);
            }
            msgSer = (ServerMessage) siobj.getObject();
        } catch (IOException | ClassNotFoundException | InvalidKeyException
                | NoSuchAlgorithmException | SignatureException | BadPaddingException
                | IllegalBlockSizeException | NoSuchPaddingException ex) {
        }

        return msgSer;
    }

    /**
     * This method allows to write a message to the server.
     *
     * @param msgCl the client'Message.
     */
    void writetoServer(ClientMessage msgCl) {

        try {
            SignedObject siobj = HandlerSecurity.getSignedMsg(msgCl, clientPrivKey);
            SealedObject seobj = HandlerSecurity.getCipheredObject(siobj, secretkey);
            out.writeObject(seobj);
            out.flush();
        } catch (IOException | InvalidKeyException | NoSuchAlgorithmException
                | SignatureException | IllegalBlockSizeException | NoSuchPaddingException ex) {
            Logger.getLogger(NetworkClient.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * This method allows to close the communication with the server.
     */
    public void disconnect() {
        ClientMessage msgCl;
        msgCl = new ClientMessage(ClientMessage_Type.DISCONNECT, "deconnection request", "null");
        this.writetoServer(msgCl);
        try {
            socket.close();
        } catch (IOException ex) {
            Logger.getLogger(NetworkClient.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
