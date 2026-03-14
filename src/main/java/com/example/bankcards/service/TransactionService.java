package com.example.bankcards.service;

import com.example.bankcards.dto.TransactionDto;
import com.example.bankcards.entity.StatusType;
import com.example.bankcards.repository.CardRepository;
import com.example.bankcards.repository.TransactionRepository;
import com.example.bankcards.util.mapper.CardMapper;
import com.example.bankcards.util.mapper.TransactionMapper;
import jakarta.ws.rs.BadRequestException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class TransactionService {
    private final TransactionRepository transactionRepository;
    private final CardMapper cardMapper;
    private final TransactionMapper transactionMapper;
    private final CardRepository cardRepository;

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
    @Transactional(propagation = Propagation.REQUIRES_NEW, isolation = Isolation.READ_COMMITTED)
    public TransactionDto create(String origin, String destination, BigDecimal amount, String email) {
        var cardFrom = cardRepository.findByPan(origin)
                .filter(entity -> entity.getStatus().equals(StatusType.ACTIVE))
                .orElseThrow(() -> new BadRequestException("Banking card " + origin + " not found or not active!"));

        var cardTo = cardRepository.findByPan(destination)
                .filter(entity -> entity.getStatus().equals(StatusType.ACTIVE))
                .orElseThrow(() -> new BadRequestException("Banking card " + destination + " not found or not active!"));

        if (amount.compareTo(BigDecimal.ZERO) < 0) {
            throw new BadRequestException("Wrong value for cash amount!");
        }

        var credit = cardMapper.cardToCardDto(cardFrom);
        credit.setPan(cardFrom.getPan());
        var debit = cardMapper.cardToCardDto(cardTo);
        debit.setPan(cardTo.getPan());

        credit.setBalance (credit.getBalance().subtract(amount));
        var fromBuilder = cardFrom.toBuilder();
        cardMapper.updateEntityFromDto (credit, fromBuilder);
        cardRepository.save (fromBuilder.build());

        debit.setBalance (debit.getBalance().add(amount));
        var toBuilder = cardTo.toBuilder();
        cardMapper.updateEntityFromDto (debit, toBuilder);
        cardRepository.save (toBuilder.build());

        var transactionDto = new TransactionDto(origin, destination, amount, null);
        ;
        var transaction = transactionRepository.save (transactionMapper.transactionDtoToTransaction (transactionDto, cardFrom, cardTo));

        return transactionMapper.transactionToTransactionDto (transaction);
    }
}
