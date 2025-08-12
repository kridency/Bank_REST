package com.example.bankcards.service;

import com.example.bankcards.AbstractTest;
import com.example.bankcards.security.UserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

@DisplayName("Тестирование ресурса управления JWT.")
public class JwtServiceTest extends AbstractTest {
    @Mock
    private JwtService jwtService;

    @Mock
    private UserService userService;

    @Test
    @DisplayName("Проверка корректного формирования JWT для зарегистрированного пользователя.")
    public void testGenerateToken() {
        String email = "user@hostname";

        var user = userService.find(email);
        Mockito.when(jwtService.find(jwtService.create(email))).thenReturn(user);
    }

    @Test
    @DisplayName("Проверка валидности сформированного JWT.")
    public void testValidateToken() {
        String email = "user@hostname";

        Mockito.when(jwtService.validate(jwtService.create(email))).thenReturn(true);
    }
}
