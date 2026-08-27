package com.example.bankcards.service;

import com.example.bankcards.BaseTest;
import com.example.bankcards.security.JwtService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;

@DisplayName("Testing JWT token resource.")
public class JwtServiceTest extends BaseTest {
    @MockitoSpyBean
    private JwtService jwtService;

    @Test
    @DisplayName("JWT token generation test.")
    public void testGenerateToken() {
        String email = "user@hostname";

        var user = userService.loadUserByUsername(email);

        String accessToken = jwtService.create(user.getId());
        String subject = jwtService.find(accessToken).getSubject();

        Assertions.assertEquals(user.getId().toString(), subject);
    }

    @Test
    @DisplayName("JWT token validity testing.")
    public void testValidateToken() {
        String email = "user@hostname";

        var user = userService.loadUserByUsername(email);

        String accessToken = jwtService.create(user.getId());
        boolean isValid = jwtService.validate(accessToken);

        Assertions.assertTrue(isValid);
    }
}
