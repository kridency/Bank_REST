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
import lombok.Synchronized;
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
     * Запускает обращение к базе данных банковских карт для получения отфильтрованного перечня.
     * Основной метод для получения ограниченной выборки из базы данных банковских карт.
     * @param email   адрес электронной почты пользователя текущей сессии
     * @param pageable  объект описания критериев пагинации перечня банковски карт
     *
     * @return  объект описания результата обращения к базе данных банковских карт
     */
    public Slice<CardDto> getFiltered(String email, Pageable pageable) {
        List<CardDto> result = cardRepository.findAll(new CardSpecification(new HashMap<>() {{
                put("owner.email", email);
        }}), pageable).stream()
                .map(cardMapper::cardToCardDto).toList();
        return new SliceImpl<>(result, pageable, result.iterator().hasNext());
    }

    /**
     * Запускает обращение к базе данных банковских карт для получения полного перечня.
     * Основной метод для получения полной выборки из базы данных банковских карт.
     * @param pageable  объект описания критериев пагинации перечня банковски карт
     *
     * @return  объект описания результата обращения к базе данных банковских карт
     */
    public Slice<CardDto> getAll(Pageable pageable) {
        List<CardDto> result = cardRepository.findAll(new CardSpecification(Collections.emptyMap()),
                        pageable).stream()
                .map(cardMapper::cardToCardDto).toList();
        return new SliceImpl<>(result, pageable, result.iterator().hasNext());
    }

    /**
     * Запускает обращение к базе данных банковских карт для создания новой записи.
     * Основной метод для внесения в базу данных банковских карт новой записи.
     * @param request   объект описания аттрибутов создаваемой записи банковской карты
     *
     * @return  объект описания результата обращения к базе данных банковских карт
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
     * Запускает обращение к базе данных банковских карт для обновления записи.
     * Основной метод для внесения изменений в базу данных банковских карт.
     * @param request   объект описания аттрибутов обновляемой записи банковской карты
     *
     * @return  объект описания результата обращения к базе данных банковских карт
     */
    @Synchronized
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
                                    throw new NullPointerException("Карта неактивна!");
                                }
                                return true;
                            })
                            .map(value -> {
                                if (new BigDecimal(card.getBalance().toString()).add(value).compareTo(BigDecimal.ZERO) < 0) {
                                    throw new NullPointerException("На карте недостаточно средств!");
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
     * Запускает обращение к базе данных банковских карт для перевода денежных средств.
     * Основной метод для перевода денежных средств между банковскими картами.
     * @param origin    номер банковской карты для списания денежных средств
     * @param destination   номер банковской карты для зачисления денежных средств
     * @param amount    количество денежных средств для перевода
     * @param email адрес электронной почты держателя банковских карт
     *
     * @return  признак успешного завершение перевода денежных средств
     */
    public boolean transfer(String origin, String destination, BigDecimal amount, String email) {
        final boolean[] result = new boolean[1];
        result[0] = true;
        var cardFrom = find(origin);
        var cardTo = find(destination);
        if (!email.equals(cardFrom.getOwner().getEmail())) {
            throw new EntityExistsException("Карта с номером " + origin + " не найдена!");
        } else if (!email.equals(cardTo.getOwner().getEmail())) {
            throw new EntityExistsException("Карта с номером " + destination + " не найдена!");
        } else if (!cardFrom.getStatus().equals(StatusType.ACTIVE)) {
            throw new BadRequestException("Перевод средств с карты с номером " + origin + " недоступен!");
        } else if (!cardTo.getStatus().equals(StatusType.ACTIVE)) {
            throw new BadRequestException("Перевод средств на карту с номером " + destination + " недоступен!");
        } else if (amount.compareTo(BigDecimal.ZERO) < 0) {
            throw new BadRequestException("Недопустимое величина денежных средств средств!");
        }

        var credit = cardMapper.cardToCardDto(cardFrom);
        credit.setPan(cardFrom.getPan());
        var debit = cardMapper.cardToCardDto(cardTo);
        debit.setPan(cardTo.getPan());


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

        return result[0];
    }

    /**
     * Запускает обращение к базе данных банковских карт для удаления записи.
     * Основной метод для удаления из базы данных записей банковских карт.
     * @param request   объект описания аттрибутов удаляемой записи банковской карты
     *
     */
    public int delete(CardDto request) {
        return cardRepository.deleteByPan(request.getPan());
    }

    /**
     * Запускает обращение к базе данных банковских карт для получения записи.
     * Вспомогательный метод для получения объекта отображения записи из базы данных
     * банковских карт.
     * @param pan   номе банковской карты (Primary Account Number)
     *
     * @return  объект отображения записи в базе данных банковских карт
     */
    private Card find(String pan) {
        return cardRepository.findByPan(pan)
                .orElseThrow(() -> new EntityNotFoundException("Банковская карта с номером = " + pan + " не найдена."));
    }
}
