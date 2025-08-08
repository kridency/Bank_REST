package com.example.bankcards.dto;

import com.example.bankcards.entity.RoleType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Ответ по запросу на регистрацию/обновление учетных данных пользователя.")
public class UserDto {
    @Schema(description = "Адрес электронной почты пользователя.")
    private String email;
    @Schema(description = "Пароль пользователя.")
    private String password;
    @Schema(description = "Перечень ролей пользователя пользователя.")
    private Set<RoleType> roles;
}
