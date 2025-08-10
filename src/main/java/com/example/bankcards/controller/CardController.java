package com.example.bankcards.controller;

import com.example.bankcards.config.property.AppProperties;
import com.example.bankcards.dto.CardDto;
import com.example.bankcards.dto.MessageDto;
import com.example.bankcards.service.CardService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/cards")
@RequiredArgsConstructor
public class CardController {
    private final AppProperties properties;
    private final CardService cardService;

    @Operation(summary = "Зарегистрировать банковскую карту",
            description = "Регистрирует нового банковскую карту.")
    @ResponseStatus(HttpStatus.OK)
    @PostMapping
    @Transactional
    @PreAuthorize("hasRole('ADMIN')")
    public CardDto registerCard(@RequestBody @Valid CardDto request) {
        return cardService.create(request);
    }

    @Operation(summary = "Обновить статус/баланс/срок действия банковской карты",
            description = "Обновляет реквизиты банковской карты.")
    @ResponseStatus(HttpStatus.OK)
    @PutMapping
    @Transactional
    @PreAuthorize("hasRole('ADMIN')")
    public CardDto updateCard(@RequestBody @Valid CardDto request) {
        return cardService.update(request);
    }

    @Operation(summary = "Получить перечень банковских карт согласно критериям фильтра",
            description = "Формирует ограниченного перечня банковских карты.")
    @ResponseStatus(HttpStatus.OK)
    @GetMapping
    @PreAuthorize("hasRole('ROLE_USER')")
    public Slice<CardDto> getCards(@RequestParam(value = "offset", required = false) Integer offset,
                                   @RequestParam(value = "limit", required = false) Integer limit,
                                   @AuthenticationPrincipal String email) {
        return cardService.getFiltered(email, PageRequest.of(Optional.ofNullable(offset).isPresent() ? offset : 0,
                Optional.ofNullable(limit).isPresent() ? limit : properties.getPaginationLimit()));
    }

    @Operation(summary = "Получить перечень всех банковских карт",
            description = "Формирует полный перечень банковских карты.")
    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/admin")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public Slice<CardDto> getAllCards(@RequestParam(value = "offset", required = false) Integer offset,
                                   @RequestParam(value = "limit", required = false) Integer limit) {
        return cardService.getAll(PageRequest.of(Optional.ofNullable(offset).isPresent() ? offset : 0,
                Optional.ofNullable(limit).isPresent() ? limit : properties.getPaginationLimit()));
    }

    @Operation(summary = "Обновить статус/баланс/срок действия банковской карты",
            description = "Обновляет реквизиты банковской карты.")
    @ResponseStatus(HttpStatus.OK)
    @DeleteMapping
    @Transactional
    @PreAuthorize("hasRole('ADMIN')")
    public MessageDto deleteCard(@RequestBody @Valid CardDto request) {
        cardService.delete(request);
        var pan = request.getPan();
        return new MessageDto("Запись банковской карты успешно удалена!",
                pan.substring(0, pan.length() - 4).replaceAll("\\d", "*")
                        + pan.substring(pan.length() - 4));
    }
}
