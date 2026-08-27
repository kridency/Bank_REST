package com.example.bankcards.service;

import com.example.bankcards.BaseTest;
import com.example.bankcards.security.CryptService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;

@DisplayName("Testing message encrypt/decrypt resource.")
public class CryptServiceTest extends BaseTest {
    @MockitoSpyBean
    private CryptService cryptService;

    @Test
    @DisplayName("Encrypt/decrypt text message.")
    public void testMessageEncryptDecrypt() {
        String message = "Hello World!";

        String encryptedMessage = cryptService.encrypt(message);
        String decryptedMessage = cryptService.decrypt(encryptedMessage);

        Assertions.assertEquals(message, decryptedMessage);
    }
}
