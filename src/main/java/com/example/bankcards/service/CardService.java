package com.example.bankcards.service;

import com.example.bankcards.dto.CardDto;
import com.example.bankcards.entity.Card;
import com.example.bankcards.entity.StatusType;
import com.example.bankcards.util.mapper.CardMapper;
import com.example.bankcards.repository.CardRepository;
import com.example.bankcards.security.UserService;
import com.example.bankcards.util.specification.CardSpecification;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import jakarta.ws.rs.BadRequestException;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.SynchronousQueue;

@Service
@RequiredArgsConstructor
public class CardService {
    private final CardRepository cardRepository;
    private final UserService userService;
    private final CardMapper cardMapper;

    private final ExecutorService executor = Executors.newFixedThreadPool(2);
    private final SynchronousQueue<BigDecimal> queue = new SynchronousQueue<>();

    /**
     * Requests banking card database for a filtered list.
     * Main banking cards database constrained sample list method.
     * @param email   current session user email address
     * @param pageable  banking card list pagination criteria object
     *
     * @return  set of banking card database record representation objects
     */
    public Slice<CardDto> getFiltered(String email, Pageable pageable) {
        List<CardDto> result = cardRepository.findAll(new CardSpecification(new HashMap<>() {{
                put("owner.email", email);
        }}), pageable).stream()
                .map(cardMapper::cardToCardDto).toList();
        return new SliceImpl<>(result, pageable, result.iterator().hasNext());
    }

    /**
     * Requests banking card database for a complete list.
     * Main banking card database complete list forming method.
     * @param pageable  banking card list pagination criteria object
     *
     * @return  set of banking card database record representation objects
     */
    public Slice<CardDto> getAll(Pageable pageable) {
        List<CardDto> result = cardRepository.findAll(new CardSpecification(Collections.emptyMap()),
                        pageable).stream()
                .map(cardMapper::cardToCardDto).toList();
        return new SliceImpl<>(result, pageable, result.iterator().hasNext());
    }

    /**
     * Requests banking card database to create new record.
     * Main banking card database record creation method.
     * @param request   new banking card data description object
     *
     * @return  banking card database record representation object
     */
    public CardDto create(CardDto request) {
        return cardMapper.cardToCardDto(cardRepository.save(new Card(
                null,
                request.getPan(),
                request.getExpireDate(),
                userService.find(request.getEmail()),
                StatusType.ACTIVE,
                Optional.ofNullable(request.getBalance()).orElse(BigDecimal.ZERO)
        )));
    }

    /**
     * Requests banking card database to update existing record.
     * Main banking card database record update method.
     * @param request   existing banking card data description object
     *
     * @return  banking card database record representation object
     */
    public CardDto update(CardDto request, String email) throws InterruptedException {
        var card = find(request.getPan());
        try {
            return cardMapper.cardToCardDto(cardRepository.save(new Card(
                    card.getId(),
                    card.getPan(),
                    card.getExpireDate(),
                    card.getOwner(),
                    Optional.ofNullable(request.getStatus()).orElse(card.getStatus()),
                    email.equals(card.getOwner().getEmail())
                            ? Optional.ofNullable(request.getBalance())
                            .filter(value -> {
                                if (!card.getStatus().equals(StatusType.ACTIVE) &&
                                        !card.getStatus().equals(StatusType.PENDING)) {
                                    throw new NullPointerException("Banking card is not active!");
                                }
                                return true;
                            })
                            .map(value -> {
                                if (new BigDecimal(card.getBalance().toString()).add(value).compareTo(BigDecimal.ZERO) < 0) {
                                    throw new NullPointerException("Insufficient cash amount!");
                                }
                                return card.getBalance().add(value);
                            })
                            .orElse(card.getBalance())
                            : card.getBalance())));
        } catch (NullPointerException e) {
            throw new InterruptedException(e.getMessage());
        }
    }

    /**
     * Requests banking card database for cash transfer between user owned accounts.
     * Main banking card cash transfer.
     * @param origin    cash source banking card number
     * @param destination   transfer destination banking card number
     * @param amount    cash transfer amount
     * @param email banking cards owner email address
     *
     * @return  successful cash transfer indicator
     */
    public boolean transfer(String origin, String destination, BigDecimal amount, String email) {
        final boolean[] result = new boolean[1];
        result[0] = true;
        var cardFrom = find(origin);
        var cardTo = find(destination);
        if (!email.equals(cardFrom.getOwner().getEmail())) {
            throw new EntityExistsException("Banking card " + origin + " not found!");
        } else if (!email.equals(cardTo.getOwner().getEmail())) {
            throw new EntityExistsException("Banking card " + destination + " not found!");
        } else if (!cardFrom.getStatus().equals(StatusType.ACTIVE)) {
            throw new BadRequestException("Cash transfer from " + origin + " unavailable!");
        } else if (!cardTo.getStatus().equals(StatusType.ACTIVE)) {
            throw new BadRequestException("Cash transfer to " + destination + " unavailable!");
        } else if (amount.compareTo(BigDecimal.ZERO) < 0) {
            throw new BadRequestException("Wrong value for cash amount!");
        }

        var credit = cardMapper.cardToCardDto(cardFrom);
        credit.setPan(cardFrom.getPan());
        var debit = cardMapper.cardToCardDto(cardTo);
        debit.setPan(cardTo.getPan());

        synchronized (cardFrom) {
            Runnable minus = () -> {
                try {
                    credit.setBalance(amount.multiply(new BigDecimal(-1)));
                    update(credit, email);
                    queue.put(amount);
                } catch (InterruptedException ex) {
                    result[0] = false;
                }
            };

            Runnable plus = () -> {
                try {
                    if (result[0]) {
                        debit.setBalance(queue.take());
                        update(debit, email);
                    }
                } catch (InterruptedException ex) {
                    result[0] = false;
                    credit.setBalance(amount.multiply(new BigDecimal(-1)));
                    try {
                        update(credit, email);
                    } catch (InterruptedException e) {
                        throw new DataIntegrityViolationException(e.getMessage());
                    }
                }
            };

            executor.execute(minus);
            executor.execute(plus);
        }

        return result[0];
    }

    /**
     * Requests banking card database to delete existing record.
     * Main banking card database record delete method.
     * @param request   existing banking card data description object
     *
     */
    public int delete(CardDto request) {
        return cardRepository.deleteByPan(request.getPan());
    }

    /**
     * Requests banking card database for the record matching specified PAN.
     * Supplementary banking card database record receive method.
     * @param pan   Primary Account Number
     *
     * @return  banking card database record representation object
     */
    private Card find(String pan) {
        return cardRepository.findByPan(pan)
                .orElseThrow(() -> new EntityNotFoundException("Banking card = " + pan + " not found."));
    }
}
