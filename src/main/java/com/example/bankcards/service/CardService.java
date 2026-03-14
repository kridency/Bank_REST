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
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

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
    @Transactional(propagation = Propagation.REQUIRES_NEW, isolation = Isolation.SERIALIZABLE)
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
    @Transactional(propagation = Propagation.REQUIRES_NEW, isolation = Isolation.READ_COMMITTED)
    public CardDto update(CardDto request) {
        var pan = Optional.ofNullable(request.getPan())
                .orElseThrow(() -> new BadRequestException("No value specified for the field PAN"));

        var card = cardRepository.findByPan(pan)
                .filter(entity -> !entity.getStatus().equals(StatusType.ACTIVE) &&
                        !entity.getStatus().equals(StatusType.PENDING))
                .orElseThrow(() -> new EntityNotFoundException("Banking card = " + pan + " not found."));

        return cardMapper.cardToCardDto(cardRepository.save(new Card(
                card.getId(),
                card.getPan(),
                card.getExpireDate(),
                card.getOwner(),
                Optional.ofNullable(request.getStatus()).orElse(card.getStatus()),
                Optional.ofNullable(request.getBalance()).filter(value ->
                                new BigDecimal(card.getBalance().toString()).add(value).compareTo(BigDecimal.ZERO) < 0)
                        .map(value -> card.getBalance().add(value))
                        .orElseThrow(() -> new NullPointerException("Insufficient cash amount!"))
        )));
    }

    /**
     * Requests banking card database to delete existing record.
     * Main banking card database record delete method.
     * @param request   existing banking card data description object
     *
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW, isolation = Isolation.READ_COMMITTED)
    public int delete(CardDto request) {
        return cardRepository.deleteByPan(request.getPan());
    }
}
