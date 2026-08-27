package com.example.bankcards.service;

import com.example.bankcards.dto.CardDto;
import com.example.bankcards.entity.StatusType;
import com.example.bankcards.repository.UserRepository;
import com.example.bankcards.util.mapper.CardMapper;
import com.example.bankcards.repository.CardRepository;
import com.example.bankcards.util.specification.GetSpecification;
import jakarta.persistence.EntityNotFoundException;
import jakarta.ws.rs.BadRequestException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CardService implements CRUDService<CardDto> {
    private final CardRepository cardRepository;
    private final UserRepository userRepository;
    private final CardMapper cardMapper;

    /**
     * Requests banking card database for a filtered list.
     * Main banking cards database constrained sample list method.
     * @param criteria   set of sought values for filter attributes
     * @param pageable  banking card list pagination criteria object
     *
     * @return  set of banking card database record representation objects
     */
    public Slice<CardDto> get(Map<String, ? extends Comparable<?>> criteria, Pageable pageable) {
        List<CardDto> result = cardRepository.findAll(new GetSpecification<>(criteria),
                pageable).stream().map(cardMapper::cardToCardDto).toList();
        return new SliceImpl<>(result, pageable, result.iterator().hasNext());
    }

    /**
     * Requests banking card database to create new record.
     * Main banking card database record creation method.
     * @param request   new banking card data description object
     *
     * @return  banking card database record representation object
     */
    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.SERIALIZABLE)
    public CardDto create(CardDto request) {
        var owner = userRepository.getByEmail (request.getEmail())
                .orElseThrow(() -> new EntityNotFoundException("User = " + request.getEmail() + " not found."));
        request.setStatus(Optional.ofNullable(request.getStatus()).orElse(StatusType.ACTIVE));
        request.setBalance(Optional.ofNullable(request.getBalance()).orElse(BigDecimal.ZERO));
        return cardMapper.cardToCardDto(cardRepository.save(cardMapper.cardDtoToCard(request, owner)));
    }

    /**
     * Requests banking card database to update existing record.
     * Main banking card database record update method.
     * @param request   existing banking card data description object
     *
     * @return  banking card database record representation object
     */
    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.READ_COMMITTED)
    public CardDto update(CardDto request) {
        var pan = Optional.ofNullable(request.getPan())
                .orElseThrow(() -> new BadRequestException("No value specified for the field PAN"));

        var card = cardRepository.findByPan(pan)
                .filter(entity -> entity.getStatus().equals(StatusType.ACTIVE) ||
                        entity.getStatus().equals(StatusType.PENDING))
                .orElseThrow(() -> new EntityNotFoundException("Banking card = " + pan + " not found."));

        request.setExpireDate(Optional.ofNullable(request.getExpireDate()).orElse(card.getExpireDate()));
        request.setStatus(Optional.ofNullable(request.getStatus()).orElse(card.getStatus()));
        request.setBalance(Optional.ofNullable(request.getBalance()).orElse(card.getBalance()));

        var cardBuilder = card.toBuilder();
        cardMapper.updateEntityFromDto(request, cardBuilder);
        return cardMapper.cardToCardDto(cardRepository.save(cardBuilder.build()));
    }

    /**
     * Requests banking card database to delete existing record.
     * Main banking card database record delete method.
     * @param request   existing banking card data description object
     *
     */
    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.READ_COMMITTED)
    public int delete(CardDto request) {
        return cardRepository.deleteByPan(request.getPan());
    }
}
