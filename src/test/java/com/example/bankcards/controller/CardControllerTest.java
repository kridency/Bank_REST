package com.example.bankcards.controller;

import com.example.bankcards.AbstractTest;
import com.example.bankcards.dto.RefreshTokenDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

public class CardControllerTest extends AbstractTest {
    @Test
    @WithMockUser(username = "user@hostname", password = "user")
    @DisplayName("Регистрация банковской карты.")
    void givenExistingUser_whenTryToRegisterCard_thenReturnCorrectResult() throws Exception {
        /*String user = "{ \"email\": \"user@hostname\", \"password\": \"user\" }";
        String token = objectMapper.readValue(
                mockMvc.perform(MockMvcRequestBuilders.post("/api/login")
                                .content(user)
                                .contentType(MediaType.APPLICATION_JSON)
                                .with(SecurityMockMvcRequestPostProcessors.csrf()))
                        .andExpect(MockMvcResultMatchers.status().isOk()).andReturn().getResponse()
                        .getContentAsByteArray(), RefreshTokenDto.class).getAccessToken();

        mockMvc.perform(MockMvcRequestBuilders.get("/api/tokens")
                        .header("Authorization", "Bearer " + token)
                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(MockMvcResultMatchers.status().isOk());*/

    }
}
