package com.example.bankcards.controller;

import com.example.bankcards.config.property.AppProperties;
import com.example.bankcards.dto.CardDto;
import com.example.bankcards.dto.MessageDto;
import com.example.bankcards.entity.StatusType;
import com.example.bankcards.service.CardService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.Optional;

@RestController
@RequestMapping("/api/cards")
@RequiredArgsConstructor
public class CardController {
    private final AppProperties properties;
    private final CardService cardService;

    @Operation(summary = "Зарегистрировать банковскую карту",
            description = "Регистрирует нового банковскую карту.")
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Transactional(propagation = Propagation.REQUIRES_NEW, isolation = Isolation.SERIALIZABLE)
    public CardDto registerCard(@RequestBody @Valid CardDto request) {
        return cardService.create(request);
    }

    @Operation(summary = "Изменить текущий статус карты.",
            description = "Обновляет статус банковской карты.")
    @ResponseStatus(HttpStatus.OK)
    @PutMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Transactional(propagation = Propagation.REQUIRES_NEW, isolation = Isolation.READ_COMMITTED)
    public CardDto updateCard(@RequestBody CardDto request, @AuthenticationPrincipal String email) throws Exception {
        return cardService.update(request, email);
    }

    @Operation(summary = "Запросить блокировку банковской карты",
            description = "Устанавливает для банковской карты статус PENDING.")
    @ResponseStatus(HttpStatus.OK)
    @PatchMapping("/block")
    @PreAuthorize("hasRole('USER')")
    public CardDto requestBlock(@RequestBody CardDto request, @AuthenticationPrincipal String email) throws Exception {
        request.setStatus(StatusType.PENDING);
        request.setBalance(null);
        return cardService.update(request, email);
    }

    @Operation(summary = "Перевести денежные средства с одной банковской карты на другую.",
            description = "Производит списание и зачисление денежных средств.")
    @ResponseStatus(HttpStatus.OK)
    @PatchMapping("/transfer")
    @PreAuthorize("hasRole('USER')")
    @Transactional(propagation = Propagation.REQUIRES_NEW, isolation = Isolation.READ_COMMITTED)
    public MessageDto transfer(@RequestParam(name = "origin") String origin,
                               @RequestParam(name = "destination") String destination,
                               @RequestParam(name = "amount") BigDecimal amount,
                               @AuthenticationPrincipal String email) {
        return cardService.transfer(origin, destination, amount, email)
                ? new MessageDto("Перевод денежных средств успешно выполнен!", "Ожидаемое завершение операции.")
                : new MessageDto("Перевод денежных средств не выполнен!", "Непредвиденное завершение операции.");
    }

    @Operation(summary = "Получить перечень банковских карт согласно критериям фильтра",
            description = "Формирует ограниченного перечня банковских карты.")
    @ResponseStatus(HttpStatus.OK)
    @GetMapping
    @PreAuthorize("hasRole('USER')")
    public Slice<CardDto> getUserCards(@RequestParam(value = "offset", required = false) Integer offset,
                                   @RequestParam(value = "limit", required = false) Integer limit,
                                   @AuthenticationPrincipal String email) {
        return cardService.getFiltered(email, PageRequest.of(Optional.ofNullable(offset).isPresent() ? offset : 0,
                Optional.ofNullable(limit).isPresent() ? limit : properties.getPaginationLimit()));
    }

    @Operation(summary = "Получить перечень всех банковских карт",
            description = "Формирует полный перечень банковских карты.")
    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/admin")
    @PreAuthorize("hasRole('ADMIN')")
    public Slice<CardDto> getAllCards(@RequestParam(value = "offset", required = false) Integer offset,
                                   @RequestParam(value = "limit", required = false) Integer limit) {
        return cardService.getAll(PageRequest.of(Optional.ofNullable(offset).isPresent() ? offset : 0,
                Optional.ofNullable(limit).isPresent() ? limit : properties.getPaginationLimit()));
    }

    @Operation(summary = "Удалить запись банковской карты из базы банных",
            description = "Удаляет банковскую карту.")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Transactional(propagation = Propagation.REQUIRES_NEW, isolation = Isolation.READ_COMMITTED)
    public MessageDto deleteCard(@RequestBody CardDto request) {
        return cardService.delete(request) == 1
                ? new MessageDto("Запись банковской карты успешно удалена!", "Ожидаемое завершение операции.")
                : new MessageDto("Запись банковской карты не найдена!", "Непредвиденное завершение операции.");
    }
}
