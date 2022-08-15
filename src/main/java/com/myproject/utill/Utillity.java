package com.myproject.utill;

import com.myproject.logger.KLogger;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.Properties;
import java.util.UUID;

public class Utillity {
    static Cipher cipher;

    public static String getUUID() {
        UUID uuid = UUID.randomUUID();
        return uuid.toString();
    }
    public static boolean stringIsEmptyOrNull(String st) {
        return st == null || st.isEmpty();
    }

    public static boolean stringIsNonEmpty(String st) {
        return st != null && !st.isEmpty();
    }

    public static int getOrDefault(int val, int dVal) {
        if (stringIsEmptyOrNull(String.valueOf(val))) {
            return dVal;
        }
        return val;
    }

    // Return boolean value from workbench property.
    public static boolean getWorkbenchValue(String key, boolean defaultValue) {
        try (FileReader reader = new FileReader("workbench.properties")) {
            Properties p = new Properties();
            p.load(reader);
            boolean checkKey = p.containsKey(key);
            if (checkKey)
                return Boolean.parseBoolean(p.getProperty(key, String.valueOf(defaultValue)));
            else {
                KLogger.error("Provided value is not present in workbench");
                throw new RuntimeException("Provided value is not present in workbench");
            }
        } catch (Exception e) {
            KLogger.error(e);
        }
        return defaultValue;
    }


    // Return string value from workbench property.
    public static String getWorkbenchValue(String key, String defaultValue) {
        try (FileReader reader = new FileReader("workbench.properties")) {
            Properties p = new Properties();
            p.load(reader);
            return (p.getProperty(key, String.valueOf(defaultValue)));
        } catch (Exception e) {
            KLogger.error(e);
        }
        return defaultValue;
    }
    public static String encrypt(String plainText, SecretKey secretKey) throws Exception {
        byte[] plainTextByte = plainText.getBytes();
        cipher.init(Cipher.ENCRYPT_MODE, secretKey);
        byte[] encryptedByte = cipher.doFinal(plainTextByte);
        Base64.Encoder encoder = Base64.getEncoder();
        String encryptedText = encoder.encodeToString(encryptedByte);
        return encryptedText;
    }

    public static String decrypt(String encryptedText, SecretKey secretKey) throws Exception {
        Base64.Decoder decoder = Base64.getDecoder();
        byte[] encryptedTextByte = decoder.decode(encryptedText);
        cipher.init(Cipher.DECRYPT_MODE, secretKey);
        byte[] decryptedByte = cipher.doFinal(encryptedTextByte);
        String decryptedText = new String(decryptedByte);
        return decryptedText;
    }
    public static String getDecrypt(String pass) throws Exception {
        KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
        keyGenerator.init(128); // block size is 128bits
        SecretKey secretKey = keyGenerator.generateKey();
        cipher = Cipher.getInstance("AES");
        return decrypt(pass,secretKey);
    }

    public static String getEncrypt(String pass) throws Exception {
        KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
        keyGenerator.init(128); // block size is 128bits
        SecretKey secretKey = keyGenerator.generateKey();
        cipher = Cipher.getInstance("AES");
        return encrypt(pass,secretKey);
    }


}
