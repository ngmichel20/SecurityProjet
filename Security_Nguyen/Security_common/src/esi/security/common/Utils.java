package esi.security.common;

import java.util.Base64;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

/**
 *
 * @author Nguyen Khanh-Michel
 */
public class Utils {

    /**
     * This method allows to get the salt.
     * 
     * @return a string
     */
    public static String getSalt() {
        byte tab[] = new byte[5];
        Random rand = ThreadLocalRandom.current();
        rand.nextBytes(tab);
        return Base64.getEncoder().encodeToString(tab);
    }
    
    public static String getStringFromByteArray(byte data[]) {
        return Base64.getEncoder().encodeToString(data);
    }

    public static byte[] getByteArrayFromString(String data) {
        return Base64.getDecoder().decode(data);
    }

    
}
