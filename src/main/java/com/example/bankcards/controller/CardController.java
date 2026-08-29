package com.example.bankcards.controller;

import com.example.bankcards.config.property.AppProperties;
import com.example.bankcards.dto.CardDto;
import com.example.bankcards.dto.MessageDto;
import com.example.bankcards.entity.StatusType;
import com.example.bankcards.service.CRUDService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Pattern;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.annotation.CurrentSecurityContext;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;
import java.util.HashMap;
import java.util.Optional;

@RestController
@RequestMapping("/api/cards")
@RequiredArgsConstructor
public class CardController {
    private final AppProperties properties;
    private final CRUDService<CardDto> service;

    @Operation(summary = "Register banking card",
            description = "Register new banking card.")
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public CardDto registerCard(@RequestBody @Valid CardDto request) {
        return service.create(request);
    }

    @Operation(summary = "Change current card status",
            description = "Renew banking card status.")
    @ResponseStatus(HttpStatus.OK)
    @PutMapping
    @PreAuthorize("hasRole('ADMIN')")
    public CardDto updateCard(@RequestBody CardDto request) {
        request.setBalance(null);
        return service.update(request);
    }

    @Operation(summary = "Request to block a credit card",
            description = "Sets status PENDING for banking card.")
    @ResponseStatus(HttpStatus.OK)
    @PatchMapping
    @PreAuthorize("hasRole('USER')")
    public CardDto blockCard(@RequestBody CardDto request) {
        request.setExpireDate(null);
        request.setStatus(StatusType.PENDING);
        request.setBalance(null);
        return service.update(request);
    }

    @Operation(summary = "List banking cards according to filter criteria",
            description = "Forms constrained list of banking cards.")
    @ResponseStatus(HttpStatus.OK)
    @GetMapping
    public Slice<CardDto> getCards(@RequestParam(value = "offset", required = false) Integer offset,
                                   @RequestParam(value = "limit", required = false) Integer limit,
                                   @AuthenticationPrincipal String email,
                                   @CurrentSecurityContext(expression = "authentication.authorities")
                                   Collection<? extends GrantedAuthority> authorities) {
        var isAdmin = authorities.stream().anyMatch(x -> x.getAuthority().equals("ROLE_ADMIN"));
        var criteria = new HashMap<String, String>(){{ put("owner.email", !isAdmin ? email : null); }};
        return service.get(criteria, PageRequest.of(Optional.ofNullable(offset).orElse(0),
                Optional.ofNullable(limit).orElse(properties.getPaginationLimit())));
    }

    @Operation(summary = "Delete banking card record from database",
            description = "Deletes banking card.")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping
    @PreAuthorize("hasRole('ADMIN')")
    public MessageDto deleteCard(@RequestParam @Pattern(
                                             regexp = "^\\d{4} \\d{4} \\d{4} \\d{4}$",
                                             message = "PAN must follow the format '#### #### #### ####'"
                                     ) String pan){
        return service.delete(new CardDto(pan, null, null, null, null)) == 1
                ? new MessageDto("Banking card record successfully deleted!", "Operation expected completion.")
                : new MessageDto("Banking card record not found!", "Operation interrupted abnormally.");
    }
}
