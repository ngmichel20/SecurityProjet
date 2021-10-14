package esi.security.common;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.Signature;
import java.security.SignatureException;
import java.security.SignedObject;
import java.util.Base64;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.KeyGenerator;
import javax.crypto.SealedObject;
import javax.crypto.SecretKey;

/**
 * This class will be the one that handle all the security parts.
 *
 * @author Nguyen Khanh-Michel
 */
public class HandlerSecurity {

    private static PublicKey serveurpubKey = null;
    private static PrivateKey serverPrivKey = null;
    private static final String ALGO_ASSYMETRIC = "RSA";

    /**
     * This method allows to get the server public key.
     *
     * @param filename
     * @return
     */
    public static PublicKey getServerPubKey(String filename) {
        if (serveurpubKey == null) {

            try {
                File file = new File(filename);
                ObjectInputStream oi = new ObjectInputStream(new FileInputStream(file));
                serveurpubKey = (PublicKey) oi.readObject();
                oi.close();
            } catch (Exception ex) {
                Logger.getLogger(HandlerSecurity.class.getName()).log(Level.SEVERE, null, ex);
            }

        }
        return serveurpubKey;
    }

    public static PrivateKey getServerPrivKey(String filename) {
        if (serverPrivKey == null) {

            try {
                File file = new File(filename);
                ObjectInputStream oi = new ObjectInputStream(new FileInputStream(file));
                serverPrivKey = (PrivateKey) oi.readObject();
                oi.close();
            } catch (Exception ex) {
                Logger.getLogger(HandlerSecurity.class.getName()).log(Level.SEVERE, null, ex);
            }

        }
        return serverPrivKey;
    }

    /**
     * This method allows the transformation array of byte to string.
     *
     * @param b byte
     * @return a string
     */
    public static String bytesToHex(byte[] b) {

        char hexDigit[] = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};

        StringBuffer buf = new StringBuffer();

        for (int j = 0; j < b.length; j++) {

            buf.append(hexDigit[(b[j] >> 4) & 0x0f]);

            buf.append(hexDigit[b[j] & 0x0f]);
        }
        return buf.toString();
    }

    /**
     * This method generate and return a pair of key.
     *
     * @return keypair
     * @throws NoSuchAlgorithmException
     * @throws NoSuchProviderException
     */
    @SuppressWarnings("empty-statement")
    public static KeyPair generateKeys() throws NoSuchAlgorithmException, NoSuchProviderException {

        KeyPairGenerator pairgen = KeyPairGenerator.getInstance(ALGO_ASSYMETRIC);
        SecureRandom random = new SecureRandom();
        pairgen.initialize(2048, random);
        KeyPair keyPair = pairgen.generateKeyPair();

        return keyPair;
    }

    /**
     * This method generate and return a secret symetric key.
     *
     * @return a secret symetric key.
     * @throws NoSuchAlgorithmException
     */
    public static SecretKey generateSecretKey() throws NoSuchAlgorithmException {
        KeyGenerator keyGen = KeyGenerator.getInstance("AES");
        keyGen.init(128);
        SecretKey key = keyGen.generateKey();
        return key;

    }

    /**
     * Take a serilizable object sign it and return a signed object with the
     * private key.
     *
     * @param object serializable object.
     * @param privatekey the private key.
     * @return a signed object with the private key.
     * @throws NoSuchAlgorithmException
     * @throws InvalidKeyException
     * @throws IOException
     * @throws SignatureException
     */
    public static SignedObject getSignedMsg(final Serializable object, PrivateKey privatekey) throws
            NoSuchAlgorithmException, InvalidKeyException, IOException, SignatureException {
        Signature signature = Signature.getInstance("SHA256withRSA");
        SignedObject so = new SignedObject(object, privatekey, signature);
        return so;
    }

    /**
     * Verify if the signature of the given parameter object is valid or not.
     *
     * @param sobj signed ocject
     * @param publickey public key
     * @return
     * @throws NoSuchAlgorithmException
     * @throws InvalidKeyException
     * @throws SignatureException
     */
    public static boolean verifySignedObject(final SignedObject sobj, PublicKey publickey) throws
            NoSuchAlgorithmException, InvalidKeyException, SignatureException {
        Signature signature = Signature.getInstance("SHA256withRSA");
        return sobj.verify(publickey, signature);
    }

    /**
     * Take a object sign it and cipher it with the secret key and return an
     * ciphered object.
     *
     * @param obj signed object
     * @param secretkey the secret key
     * @return the ciphered object
     * @throws NoSuchAlgorithmException
     * @throws NoSuchPaddingException
     * @throws InvalidKeyException
     * @throws IOException
     * @throws IllegalBlockSizeException
     */
    public static SealedObject getCipheredObject(final SignedObject obj, SecretKey secretkey) throws
            NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IOException, IllegalBlockSizeException {

        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.ENCRYPT_MODE, secretkey);
        return new SealedObject(obj, cipher);
    }

    /**
     * take a ciphered object decipher it and return a signed object
     *
     * @param obj
     * @param secretkey
     * @return a signed object
     * @throws NoSuchAlgorithmException
     * @throws NoSuchPaddingException
     * @throws InvalidKeyException
     * @throws IOException
     * @throws ClassNotFoundException
     * @throws IllegalBlockSizeException
     * @throws BadPaddingException
     */
    public static SignedObject getUnCipheredObject(final SealedObject obj, SecretKey secretkey) throws
            NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IOException, ClassNotFoundException, IllegalBlockSizeException, BadPaddingException {
        SignedObject so;
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.DECRYPT_MODE, secretkey);
        so = (SignedObject) obj.getObject(cipher);
        return so;
    }

    /**
     * Take a password and its salt, hash it with the sha-1 algorithm and return
     * a string
     *
     * @param pwd the password
     * @param salt the salt
     * @return a string
     */
    public static String getHash(String pwd, String salt) {
        byte[] hash = null;
        try {
            pwd += salt;
            byte[] enc = Base64.getEncoder().encode(pwd.getBytes("utf-8"));
            MessageDigest mdg = MessageDigest.getInstance("SHA-256");
            hash = mdg.digest(enc);
        } catch (UnsupportedEncodingException | NoSuchAlgorithmException ex) {
            Logger.getLogger(HandlerSecurity.class.getName()).log(Level.SEVERE, null, ex);
        }
        return Base64.getEncoder().encodeToString(hash);
    }

    /**
     * Verify if the password given in parameter is valid or not
     *
     * @param pwddHashed the hashed password.
     * @param pwd the password.
     * @param salt the salt.
     * @return true if the pwd was good.
     */
    public static boolean isgoodPwd(String pwddHashed, String pwd, String salt) {
        boolean ok = false;
        try {

            pwd += salt;
            byte[] enc = Base64.getEncoder().encode(pwd.getBytes("utf-8"));
            MessageDigest mdg = MessageDigest.getInstance("SHA-256");
            enc = mdg.digest(enc);
            String hash = Base64.getEncoder().encodeToString(enc);

            ok = pwddHashed.equals(hash);

        } catch (UnsupportedEncodingException | NoSuchAlgorithmException ex) {
            Logger.getLogger(HandlerSecurity.class.getName()).log(Level.SEVERE, null, ex);
        }
        return ok;
    }

    /**
     * This method allows to store the server public key in a file.
     *
     * @param key the publicKey
     * @param file the file.
     */
    public static void storeServerPubKey(PublicKey key, String file) {

        try {
            File f = new File(file);
            ObjectOutputStream ou = new ObjectOutputStream(new FileOutputStream(f));
            ou.writeObject(key);
            ou.close();
        } catch (Exception e) {
        }
    }

    /**
     * This method allows to store the server private key in a file.
     *
     * @param key the privateKey
     * @param file the file.
     */
    public static void storeServerPrivKey(PrivateKey key, String file) {

        try {
            File f = new File(file);
            ObjectOutputStream ou = new ObjectOutputStream(new FileOutputStream(f));
            ou.writeObject(key);
            ou.close();
        } catch (Exception e) {
        }
    }

}
