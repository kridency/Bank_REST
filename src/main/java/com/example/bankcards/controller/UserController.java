package com.example.bankcards.controller;

import com.example.bankcards.config.property.AppProperties;
import com.example.bankcards.dto.MessageDto;
import com.example.bankcards.dto.UserDto;
import com.example.bankcards.service.CRUDService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Optional;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {
    private final AppProperties properties;
    private final CRUDService<UserDto> service;

    @Operation(summary = "List users according to filter criteria",
            description = "Forms constrained list of user records.")
    @ResponseStatus(HttpStatus.OK)
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public Slice<UserDto> getUsers(@RequestBody(required = false) UserDto request,
                                   @RequestParam(value = "offset", required = false) Integer offset,
                                   @RequestParam(value = "limit", required = false) Integer limit) {
        var criteria = new HashMap<String, String>(){{
            put("email", Optional.ofNullable(request).map(UserDto::getEmail).orElse(null));
        }};
        return service.get(criteria, PageRequest.of(Optional.ofNullable(offset).orElse(0),
                Optional.ofNullable(limit).orElse(properties.getPaginationLimit())));
    }

    @Operation(summary = "Register user",
            description = "Register new user.")
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public MessageDto registerUser(@RequestBody @Valid UserDto request) {
        service.create(request);
        return new MessageDto("User successfully created!", request.getEmail());
    }

    @Operation(summary = "Update user credentials",
            description = "Updates e-mail address, password and roles.")
    @ResponseStatus(HttpStatus.OK)
    @PutMapping
    @PreAuthorize("hasRole('ADMIN')")
    public UserDto updateUser(@RequestBody @Valid UserDto request) {
        return service.update(request);
    }

    @Operation(summary = "Delete user credentials",
            description = "Deletes user credentials.")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping
    @PreAuthorize("hasRole('ADMIN')")
    public MessageDto deleteUser(@RequestBody UserDto request) {
        return service.delete(request) == 1
                ? new MessageDto("User record deleted successfully!", "Operation expected completion.")
                : new MessageDto("User record not found!", "Operation interrupted abnormally.");
    }
}
