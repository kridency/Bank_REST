package com.example.bankcards.controller;

import com.example.bankcards.BaseTest;
import com.example.bankcards.dto.UserDto;
import com.fasterxml.jackson.core.type.TypeReference;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.List;
import java.util.Map;

@DisplayName("Testing user account management resource.")
public class UserControllerTest extends BaseTest {
    @Test
    @WithUserDetails(value = "admin@hostname")
    @DisplayName("User records complete list.")
    void givenAdminUser_whenTryToGetAllUsers_thenReturnCorrectResult() throws Exception {
        var slice = objectMapper.readValue(mockMvc.perform(MockMvcRequestBuilders.get("/api/users")
                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(MockMvcResultMatchers.status().isOk()).andReturn().getResponse()
                .getContentAsByteArray(), new TypeReference<Map<String, Object>>() {});

        var userList = objectMapper.convertValue(slice.get("content"), new TypeReference<List<UserDto>>() {});

        Assertions.assertEquals(2, userList.size());
    }

    @Test
    @WithUserDetails(value = "admin@hostname")
    @DisplayName("User account creation.")
    void givenNewUserCredentials_whenTryToCreate_thenReturnCorrectResult() throws Exception {
        String newUser = "{ \"email\": \"test@hostname\", \"password\": \"test\" }";
        mockMvc.perform(MockMvcRequestBuilders.post("/api/users")
                        .content(newUser)
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("User successfully created!"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.description").value("test@hostname"));

        mockMvc.perform(MockMvcRequestBuilders.post("/api/login")
                        .content(newUser)
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    @WithUserDetails(value = "admin@hostname")
    @DisplayName("User account update.")
    void givenExistingUser_whenTryToUpdate_thenReturnCorrectResult() throws Exception {
        String updateUser = "{ \"email\": \"user@hostname\", \"password\": \"test\" }";
        mockMvc.perform(MockMvcRequestBuilders.put("/api/users")
                        .content(updateUser)
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.email").value("user@hostname"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.roles").value("ROLE_USER"));

        mockMvc.perform(MockMvcRequestBuilders.post("/api/login")
                        .content(updateUser)
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    @WithUserDetails(value = "admin@hostname")
    @DisplayName("User account delete.")
    void givenExistingUser_whenTryToDelete_thenReturnsCorrectResult() throws Exception {
        String deleteUser = "{ \"email\": \"user@hostname\"}";
        mockMvc.perform(MockMvcRequestBuilders.delete("/api/users")
                        .content(deleteUser)
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(MockMvcResultMatchers.status().isNoContent())
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("User record deleted successfully!"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.description").value("Operation expected completion."));
    }
}
