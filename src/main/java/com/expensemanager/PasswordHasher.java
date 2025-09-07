package com.expensemanager;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.SecureRandom;
import java.security.spec.KeySpec;
import java.util.Base64;

public class PasswordHasher {
    private static final int ITERATIONS = 65536;
    private static final int KEY_LENGTH = 256;
    private static final int SALT_LENGTH = 16;

    public static String hash(String password) throws Exception {
        SecureRandom random = new SecureRandom();
        byte[] salt = new byte[SALT_LENGTH];
        random.nextBytes(salt);
        
        KeySpec spec = new PBEKeySpec(password.toCharArray(), salt, ITERATIONS, KEY_LENGTH);
        SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
        byte[] hash = factory.generateSecret(spec).getEncoded();
        
        return Base64.getEncoder().encodeToString(salt) + ":" + 
               Base64.getEncoder().encodeToString(hash);
    }

    public static boolean verify(String password, String storedHash) throws Exception {
        String[] parts = storedHash.split(":");
        byte[] salt = Base64.getDecoder().decode(parts[0]);
        byte[] storedHashBytes = Base64.getDecoder().decode(parts[1]);
        
        KeySpec spec = new PBEKeySpec(password.toCharArray(), salt, ITERATIONS, KEY_LENGTH);
        SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
        byte[] testHash = factory.generateSecret(spec).getEncoded();
        
        // Time-constant comparison
        int diff = storedHashBytes.length ^ testHash.length;
        for(int i = 0; i < storedHashBytes.length && i < testHash.length; i++) {
            diff |= storedHashBytes[i] ^ testHash[i];
        }
        return diff == 0;
    }
}