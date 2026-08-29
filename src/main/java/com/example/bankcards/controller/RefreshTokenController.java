package com.example.bankcards.controller;

import com.example.bankcards.dto.RefreshTokenDto;
import com.example.bankcards.entity.User;
import com.example.bankcards.service.RefreshTokenService;
import com.example.bankcards.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/tokens")
@RequiredArgsConstructor
public class RefreshTokenController {
    private final UserService userService;
    private final RefreshTokenService tokenService;

    @Operation(summary = "Refresh access token for an authenticated user",
            description = "Refreshes previously authenticated user access token.")
    @ResponseStatus(HttpStatus.OK)
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    public RefreshTokenDto reissueToken(@AuthenticationPrincipal String username) {
        User user = userService.loadUserByUsername(username);
        return tokenService.update(user.getId());
    }
}
