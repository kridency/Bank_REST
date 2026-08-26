package com.example.bankcards.service;

import com.example.bankcards.BaseTest;
import com.example.bankcards.security.UserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

@DisplayName("Testing JWT token resource.")
public class JwtServiceTest extends BaseTest {
    @Mock
    private JwtService jwtService;

    @Mock
    private UserService userService;

    @Test
    @DisplayName("JWT token generation test.")
    public void testGenerateToken() {
        String email = "user@hostname";

        var user = userService.find(email);
        Mockito.when(jwtService.find(jwtService.create(email))).thenReturn(user);
    }

    @Test
    @DisplayName("JWT token validity testing.")
    public void testValidateToken() {
        String email = "user@hostname";

        Mockito.when(jwtService.validate(jwtService.create(email))).thenReturn(true);
    }
}
