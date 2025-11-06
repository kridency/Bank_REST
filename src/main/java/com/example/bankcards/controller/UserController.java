package com.example.bankcards.controller;

import com.example.bankcards.dto.MessageDto;
import com.example.bankcards.dto.UserDto;
import com.example.bankcards.security.UserService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @Operation(summary = "Register user",
            description = "Register new user.")
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.REPEATABLE_READ)
    @PreAuthorize("hasRole('ADMIN')")
    public MessageDto registerUser(@RequestBody @Valid UserDto request) {
        userService.create(request);
        return new MessageDto("User successfully created!", request.getEmail());
    }

    @Operation(summary = "Update user credentials",
            description = "Updates e-mail address, password and roles.")
    @ResponseStatus(HttpStatus.OK)
    @PutMapping
    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.REPEATABLE_READ)
    @PreAuthorize("hasRole('ADMIN')")
    public UserDto updateUser(@RequestBody @Valid UserDto request) {
        return userService.update(request);
    }

    @Operation(summary = "Delete user credentials",
            description = "Deletes user credentials.")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping
    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.REPEATABLE_READ)
    @PreAuthorize("hasRole('ADMIN')")
    public MessageDto deleteUser(@RequestBody UserDto request) {
        return userService.delete(request) == 1
                ? new MessageDto("User record deleted successfully!", "Operation expected completion.")
                : new MessageDto("User record not found!", "Operation interrupted abnormally.");
    }
}
