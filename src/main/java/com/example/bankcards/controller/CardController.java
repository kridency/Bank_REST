package com.example.bankcards.controller;

import com.example.bankcards.config.property.AppProperties;
import com.example.bankcards.dto.CardDto;
import com.example.bankcards.dto.MessageDto;
import com.example.bankcards.dto.TransactionDto;
import com.example.bankcards.entity.StatusType;
import com.example.bankcards.entity.Transaction;
import com.example.bankcards.service.CardService;
import com.example.bankcards.service.TransactionService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.Optional;

@RestController
@RequestMapping("/api/cards")
@RequiredArgsConstructor
public class CardController {
    private final AppProperties properties;
    private final CardService cardService;
    private final TransactionService transactionService;

    @Operation(summary = "Register banking card",
            description = "Register new banking card.")
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public CardDto registerCard(@RequestBody @Valid CardDto request) {
        return cardService.create(request);
    }

    @Operation(summary = "Change current card status",
            description = "Renew banking card status.")
    @ResponseStatus(HttpStatus.OK)
    @PutMapping
    @PreAuthorize("hasRole('ADMIN')")
    public CardDto updateCard(@RequestBody CardDto request) throws Exception {
        request.setBalance(null);
        return cardService.update(request);
    }

    @Operation(summary = "Block banking card",
            description = "Sets status PENDING for banking card.")
    @ResponseStatus(HttpStatus.OK)
    @PatchMapping("/block")
    @PreAuthorize("hasRole('USER')")
    public CardDto requestBlock(@RequestBody CardDto request) throws Exception {
        request.setStatus(StatusType.PENDING);
        request.setBalance(null);
        return cardService.update(request);
    }

    @Operation(summary = "Transfer cash from one banking card to another",
            description = "Conduct cash withdrawal and deposit.")
    @ResponseStatus(HttpStatus.OK)
    @PatchMapping("/transfer")
    @PreAuthorize("hasRole('USER')")
    public TransactionDto transfer(@RequestParam(name = "origin") String origin,
                                       @RequestParam(name = "destination") String destination,
                                       @RequestParam(name = "amount") BigDecimal amount,
                                       @AuthenticationPrincipal String email) {
        return transactionService.create(origin, destination, amount, email);
    }

    @Operation(summary = "List banking cards according to filter criteria",
            description = "Forms constrained list of banking cards.")
    @ResponseStatus(HttpStatus.OK)
    @GetMapping
    @PreAuthorize("hasRole('USER')")
    public Slice<CardDto> getUserCards(@RequestParam(value = "offset", required = false) Integer offset,
                                   @RequestParam(value = "limit", required = false) Integer limit,
                                   @AuthenticationPrincipal String email) {
        return cardService.getFiltered(email, PageRequest.of(Optional.ofNullable(offset).orElse(0),
                Optional.ofNullable(limit).orElse(properties.getPaginationLimit())));
    }

    @Operation(summary = "List all banking cards",
            description = "Forms complete list of banking cards.")
    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/admin")
    @PreAuthorize("hasRole('ADMIN')")
    public Slice<CardDto> getAllCards(@RequestParam(value = "offset", required = false) Integer offset,
                                   @RequestParam(value = "limit", required = false) Integer limit) {
        return cardService.getAll(PageRequest.of(Optional.ofNullable(offset).orElse(0),
                Optional.ofNullable(limit).orElse(properties.getPaginationLimit())));
    }

    @Operation(summary = "Delete banking card record from database",
            description = "Deletes banking card.")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping
    @PreAuthorize("hasRole('ADMIN')")
    public MessageDto deleteCard(@RequestBody CardDto request) {
        return cardService.delete(request) == 1
                ? new MessageDto("Banking card record successfully deleted!", "Operation expected completion.")
                : new MessageDto("Запись банковской карты не найдена!", "Operation interrupted abnormally.");
    }
}
