package com.example.bankcards.controller;

import com.example.bankcards.dto.RefreshTokenDto;
import com.example.bankcards.service.RefreshTokenService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/tokens")
@RequiredArgsConstructor
public class RefreshTokenController {
    private final RefreshTokenService tokenService;

    @Operation(summary = "Обновить электронный пропуск аутентифицированного пользователя",
            description = "Обновляет электронный пропуск аутентифицированного пользователя.")
    @ResponseStatus(HttpStatus.OK)
    @GetMapping
    @Transactional
    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    public RefreshTokenDto updateToken(@AuthenticationPrincipal String username) {
        return tokenService.update(username);
    }
}
