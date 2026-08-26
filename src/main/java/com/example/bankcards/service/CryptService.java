package com.example.bankcards.service;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import java.util.Base64;

@Component
public class CryptService {
    private static final String ALGORITHM = "AES/CBC/PKCS5Padding";
    private static final byte[] STATIC_IV = {
            0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07, 0x08,
            0x09, 0x0A, 0x0B, 0x0C, 0x0D, 0x0E, 0x0F, 0x10
    };

    private final SecretKey secretKey;

    public CryptService(@Qualifier("dataEncryptionKey") SecretKey secretKey) {
        this.secretKey = secretKey;
    }

    /**
     * Encrypts message according to the key from application properties.
     * Main text message encryption method.
     * @param message   message to encrypt
     *
     * @return  encrypted message
     */
    public String encrypt(String message) {
        if (message == null || secretKey == null) return null;
        try {
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            IvParameterSpec ivSpec = new IvParameterSpec(STATIC_IV);

            cipher.init(Cipher.ENCRYPT_MODE, secretKey, ivSpec);
            byte[] cipherText = cipher.doFinal(message.getBytes());

            return Base64.getEncoder().encodeToString(cipherText);
        } catch (Exception e) {
            throw new RuntimeException("Deterministic encryption failed", e);
        }
    }

    /**
     * Decrypts message according to the key from application properties.
     * Main text message decrypting method.
     * @param cipherTextBase64   method to decrypt
     *
     * @return  decrypted message
     */
    public String decrypt(String cipherTextBase64) {
        if (cipherTextBase64 == null || secretKey == null) return null;
        try {
            byte[] cipherText = Base64.getDecoder().decode(cipherTextBase64);

            Cipher cipher = Cipher.getInstance(ALGORITHM);
            IvParameterSpec ivSpec = new IvParameterSpec(STATIC_IV);

            cipher.init(Cipher.DECRYPT_MODE, secretKey, ivSpec);
            return new String(cipher.doFinal(cipherText));
        } catch (Exception e) {
            throw new RuntimeException("Deterministic decryption failed", e);
        }
    }
}
