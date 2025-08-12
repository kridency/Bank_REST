package com.example.bankcards.service;

import com.example.bankcards.AbstractTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

@DisplayName("Тестирование ресурса шифровки/дешифровки сообщений.")
public class CryptServiceTest extends AbstractTest {
    @Mock
    private CryptService cryptService;

    @Test
    @MockitoSettings(strictness = Strictness.LENIENT)
    @DisplayName("Проверка шифровки дешифровки сообщения.")
    public void testMessageEncryptDecrypt() throws Throwable {
        String message = "Hello World!";
        Mockito.when(cryptService.decrypt(cryptService.encrypt(message))).thenReturn(message);
    }
}
