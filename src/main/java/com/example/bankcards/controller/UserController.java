package com.example.bankcards.controller;

import com.example.bankcards.dto.MessageDto;
import com.example.bankcards.dto.RegistrationDto;
import com.example.bankcards.dto.UserDto;
import com.example.bankcards.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @Operation(summary = "Зарегистрировать пользователя",
            description = "Регистрирует нового пользователя.")
    @ResponseStatus(HttpStatus.OK)
    @PostMapping
    @Transactional
    @PreAuthorize("hasRole('ADMIN')")
    public MessageDto registerUser(@RequestBody @Valid RegistrationDto request) {
        userService.create(request);
        return new MessageDto("User successfully created!", request.getEmail());
    }

    @Operation(summary = "Обновить учетные дынные пользователя",
            description = "Обновляет адрес электронной почты, пароль и перечень ролей пользователя.")
    @ResponseStatus(HttpStatus.OK)
    @PutMapping
    @Transactional
    @PreAuthorize("hasRole('ADMIN')")
    public UserDto updateUser(@RequestBody @Valid RegistrationDto request, @AuthenticationPrincipal String username) {
        return userService.update(request, username);
    }

    @Operation(summary = "Удалить учетные данные пользователя",
            description = "Удаляет учетные данные пользователя.")
    @ResponseStatus(HttpStatus.OK)
    @DeleteMapping
    @Transactional
    @PreAuthorize("hasRole('ADMIN')")
    public UserDto deleteUser(@AuthenticationPrincipal String username) {
        return userService.delete(username);
    }
}
