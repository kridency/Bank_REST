package com.example.bankcards.controller;

import com.example.bankcards.dto.TransactionDto;
import com.example.bankcards.service.TransactionService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
@RequestMapping("/api/transfers")
@RequiredArgsConstructor
public class TransactionController {
    private final TransactionService transactionService;

    @Operation(summary = "Transfer cash from one banking card to another",
            description = "Conduct cash withdrawal and deposit.")
    @ResponseStatus(HttpStatus.OK)
    @PostMapping
    @PreAuthorize("hasRole('USER')")
    public TransactionDto transfer(@RequestParam(name = "origin") String origin,
                                   @RequestParam(name = "destination") String destination,
                                   @RequestParam(name = "amount") BigDecimal amount,
                                   @AuthenticationPrincipal String email) {
        return transactionService.create(origin, destination, amount, email);
    }
}
