package com.example.bankcards.service;

import com.example.bankcards.config.property.AppProperties;
import org.springframework.stereotype.Component;

import javax.crypto.*;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;

@Component
public class CryptService {
    private final Cipher encCipher;
    private final Cipher decCipher;

    public CryptService(AppProperties properties) throws Exception {
        byte[] panKey = Base64.getDecoder().decode(properties.getPanKey());
        SecretKey key = new SecretKeySpec(panKey, 0,panKey.length, "DES");
        encCipher = Cipher.getInstance("DES/ECB/PKCS5Padding");
        decCipher = Cipher.getInstance("DES/ECB/PKCS5Padding");
        encCipher.init(Cipher.ENCRYPT_MODE, key);
        decCipher.init(Cipher.DECRYPT_MODE, key);
    }

    /**
     * Шифрует сообщение согласно ключу, указанному в настройках приложения.
     * Основной метод для шифрования текстовых сообщений.
     * @param message   сообщение, подлежащее шифровке
     *
     * @return  зашифрованное сообщение
     */
    public String encrypt(String message) throws Throwable {
        return Base64.getEncoder().encodeToString(encCipher.doFinal(message.getBytes()));
    }

    /**
     * Дешифрует сообщение согласно ключу, указанному в настройках приложения.
     * Основной метод для дешифрации текстовых сообщений.
     * @param message   сообщение, подлежащее дешифровке
     *
     * @return  незашифрованное сообщение
     */
    public String decrypt(String message) throws Throwable {
        return new String(decCipher.doFinal(Base64.getDecoder().decode(message)));
    }
}
