package com.example.bankcards.dto;

import com.example.bankcards.entity.RoleType;
import jakarta.validation.constraints.Email;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Schema(description = "Запрос на создание/обновление учетных данных пользователя.")
public class RegistrationDto {
    @NotNull(message = "Не указано значение для поля email.")
    @Email(message = "Недопустимое значение для адреса электронной почты.")
    @Schema(description = "Адрес электронной почты пользователя.")
    private String email;
    @NotNull(message = "Не указано значение для поля password.")
    @Schema(description = "Пароль пользователя.")
    private String password;
    @Schema(description = "Перечень ролей пользователя.")
    private Set<RoleType> roles;
}
