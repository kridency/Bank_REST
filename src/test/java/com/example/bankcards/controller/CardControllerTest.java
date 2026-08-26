package com.example.bankcards.controller;

import com.example.bankcards.AbstractTest;
import com.example.bankcards.dto.CardDto;
import com.example.bankcards.dto.RefreshTokenDto;
import com.fasterxml.jackson.core.type.TypeReference;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.core.annotation.Order;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.List;
import java.util.Map;

@DisplayName("Testing banking card managing resource.")
public class CardControllerTest extends AbstractTest {
    @Test
    @WithUserDetails(value = "admin@hostname")
    @DisplayName("Register banking card.")
    void givenExistingUser_whenTryToRegisterCard_thenReturnCorrectResult() throws Exception {
        String newCard = "{ \"pan\": \"4276 1234 5088 9012\", " +
                "\"expire_date\": \"2027-02\", " +
                "\"email\": \"user@hostname\", " +
                "\"balance\": 200.0 }";

        mockMvc.perform(MockMvcRequestBuilders.post("/api/cards")
                        .content(newCard)
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.jsonPath("$.pan").value("**** **** **** 9012"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.expire_date").value("2027-02"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.email").value("user@hostname"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.status").value("ACTIVE"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.balance").value("200.0"));

    }

    @Test
    @WithUserDetails(value = "admin@hostname")
    @DisplayName("Banking cards complete list.")
    void givenAdminUser_whenTryToGetAllCards_thenReturnCorrectResult() throws Exception {

        var slice = objectMapper.readValue(mockMvc.perform(MockMvcRequestBuilders.get("/api/cards/admin")
                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(MockMvcResultMatchers.status().isOk()).andReturn().getResponse()
                .getContentAsByteArray(), new TypeReference<Map<String, Object>>() {});

        var cardList = objectMapper.convertValue(slice.get("content"), new TypeReference<List<CardDto>>() {});

        Assertions.assertEquals(3, cardList.size());
    }

    @Order(2)
    @Test
    @WithUserDetails(value = "user@hostname")
    @DisplayName("Receive banking card list for the specific user.")
    void givenUser_whenTryToGetUserCards_thenReturnCorrectResult() throws Exception {
        String user = "{ \"email\": \"user@hostname\", \"password\": \"user\" }";
        String token = objectMapper.readValue(
                mockMvc.perform(MockMvcRequestBuilders.post("/api/login")
                                .content(user)
                                .contentType(MediaType.APPLICATION_JSON)
                                .with(SecurityMockMvcRequestPostProcessors.csrf()))
                        .andExpect(MockMvcResultMatchers.status().isOk()).andReturn().getResponse()
                        .getContentAsByteArray(), RefreshTokenDto.class).getAccessToken();

        var slice = objectMapper.readValue(mockMvc.perform(MockMvcRequestBuilders.get("/api/cards")
                        .header("Authorization", "Bearer " + token)
                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(MockMvcResultMatchers.status().isOk()).andReturn().getResponse()
                .getContentAsByteArray(), new TypeReference<Map<String, Object>>() {});

        var cardList = objectMapper.convertValue(slice.get("content"), new TypeReference<List<CardDto>>() {});

        Assertions.assertEquals(2, cardList.size());
    }

    @Test
    @WithUserDetails(value = "user@hostname")
    @DisplayName("Transfer cash between banking cards.")
    void givenExistingCard_whenTryToTransfer_thenReturnCorrectResult() throws Exception {
        String user = "{ \"email\": \"user@hostname\", \"password\": \"user\" }";
        String token = objectMapper.readValue(
                mockMvc.perform(MockMvcRequestBuilders.post("/api/login")
                                .content(user)
                                .contentType(MediaType.APPLICATION_JSON)
                                .with(SecurityMockMvcRequestPostProcessors.csrf()))
                        .andExpect(MockMvcResultMatchers.status().isOk()).andReturn().getResponse()
                        .getContentAsByteArray(), RefreshTokenDto.class).getAccessToken();

        mockMvc.perform(MockMvcRequestBuilders.patch("/api/cards/transfer")
                        .param("origin", "4276 1234 5078 9012")
                        .param("destination", "4276 1234 5008 9012")
                        .param("amount", "30.0")
                        .header("Authorization", "Bearer " + token)
                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.amount").value("30.0"));

    }

    @Test
    @WithUserDetails(value = "user@hostname")
    @DisplayName("Request banking card block.")
    void givenExistingCard_whenTryToRequestBlock_thenReturnCorrectResult() throws Exception {
        String user = "{ \"email\": \"user@hostname\", \"password\": \"user\" }";
        String token = objectMapper.readValue(
                mockMvc.perform(MockMvcRequestBuilders.post("/api/login")
                                .content(user)
                                .contentType(MediaType.APPLICATION_JSON)
                                .with(SecurityMockMvcRequestPostProcessors.csrf()))
                        .andExpect(MockMvcResultMatchers.status().isOk()).andReturn().getResponse()
                        .getContentAsByteArray(), RefreshTokenDto.class).getAccessToken();

        String updateCard = "{ \"pan\": \"4276 1234 5008 9012\" }";

        mockMvc.perform(MockMvcRequestBuilders.patch("/api/cards/block")
                        .header("Authorization", "Bearer " + token)
                        .content(updateCard)
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.pan").value("**** **** **** 9012"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.expire_date").value("2026-11"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.email").value("user@hostname"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.status").value("PENDING"));

    }

    @Test
    @WithUserDetails(value = "admin@hostname")
    @DisplayName("Update banking card status.")
    void givenExistingCard_whenTryToSetStatusBLOCKED_thenReturnCorrectResult() throws Exception {
        String user = "{ \"email\": \"admin@hostname\", \"password\": \"admin\" }";
        String token = objectMapper.readValue(
                mockMvc.perform(MockMvcRequestBuilders.post("/api/login")
                                .content(user)
                                .contentType(MediaType.APPLICATION_JSON)
                                .with(SecurityMockMvcRequestPostProcessors.csrf()))
                        .andExpect(MockMvcResultMatchers.status().isOk()).andReturn().getResponse()
                        .getContentAsByteArray(), RefreshTokenDto.class).getAccessToken();

        String updateCard = "{ \"pan\": \"4276 1234 5008 9012\", " +
                "\"expire_date\": \"2026-11\", " +
                "\"email\": \"user@hostname\", " +
                "\"status\": \"BLOCKED\" }";

        mockMvc.perform(MockMvcRequestBuilders.put("/api/cards")
                        .header("Authorization", "Bearer " + token)
                        .content(updateCard)
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.pan").value("**** **** **** 9012"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.expire_date").value("2026-11"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.email").value("user@hostname"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.status").value("BLOCKED"));

    }
}
