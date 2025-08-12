package com.example.bankcards.controller;

import com.example.bankcards.AbstractTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

@DisplayName("Тестирование ресурса управления учетными записями пользователей.")
public class UserControllerTest extends AbstractTest {
    @Test
    @WithUserDetails(value = "admin@hostname")
    @DisplayName("Создание учетной записи пользователя.")
    void givenNewUserCredentials_whenTryToCreate_thenReturnCorrectResult() throws Exception {
        String newUser = "{ \"email\": \"test@hostname\", \"password\": \"test\" }";
        mockMvc.perform(MockMvcRequestBuilders.post("/api/users")
                        .content(newUser)
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("Пользователь успешно зарегистрирован!"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.description").value("test@hostname"));

        mockMvc.perform(MockMvcRequestBuilders.post("/api/login")
                        .content(newUser)
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    @WithUserDetails(value = "admin@hostname")
    @DisplayName("Изменение учетной записи пользователя.")
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
    @DisplayName("Удаление учетной записи пользователя.")
    void givenExistingUser_whenTryToDelete_thenReturnsCorrectResult() throws Exception {
        String deleteUser = "{ \"email\": \"user@hostname\"}";
        mockMvc.perform(MockMvcRequestBuilders.delete("/api/users")
                        .content(deleteUser)
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(MockMvcResultMatchers.status().isNoContent())
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("Учетная запись пользователя успешно удалена!"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.description").value("Ожидаемое завершение операции"));
    }
}
