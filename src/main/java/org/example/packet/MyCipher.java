package org.example.packet;

import lombok.Builder;
import lombok.Data;

import javax.crypto.*;
import javax.crypto.spec.SecretKeySpec;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

@Data
@Builder(toBuilder = true)
public class MyCipher {

    private static Cipher myCipher;
    private static final String key = "ThisIsASecretKey";
    private static SecretKey secretKey;

    static {
        try {
            // initialization
            myCipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            secretKey = new SecretKeySpec(key.getBytes(), "AES");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        }
    }

    private static byte[] encodes(byte[] string) {
        byte[] encodedStr = new byte[0];
        try {
            synchronized (myCipher) {
                myCipher.init(Cipher.ENCRYPT_MODE, secretKey);
                encodedStr = myCipher.doFinal(string);
            }
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        }
        return encodedStr;
    }

    private static byte[] decodes(byte[] string) {
        byte[] decodedStr = new byte[0];
        try {
            synchronized (myCipher) {
                myCipher.init(Cipher.DECRYPT_MODE, secretKey);
                decodedStr = myCipher.doFinal(string);
            }
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        }
        return decodedStr;
    }

    public static byte[] encode(byte[] string) {
        return encodes(string);
    }

    public static byte[] decode(byte[] string) {
        return decodes(string);
    }
}