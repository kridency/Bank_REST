package com.example.bankcards.util.mapper;

import com.example.bankcards.dto.CardDto;
import com.example.bankcards.dto.TransactionDto;
import com.example.bankcards.entity.Card;
import com.example.bankcards.entity.Transaction;
import com.example.bankcards.entity.User;
import org.mapstruct.*;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = "spring",
        uses = { CardMapper.class })
@Named("TransactionMapper")
public interface TransactionMapper {
    @Mappings({
            @Mapping(source = "from.pan", target = "from", qualifiedByName = { "CardMapper", "maskPan" } ),
            @Mapping(source = "to.pan", target = "to", qualifiedByName = { "CardMapper", "maskPan" } ),
            @Mapping(source = "amount", target = "amount"),
            @Mapping(source = "createdAt", target = "createdAt")
    })
    TransactionDto transactionToTransactionDto (Transaction transaction);

    @Mappings({
            @Mapping(target = "id", ignore = true),
            @Mapping(source = "from", target = "from"),
            @Mapping(source = "to", target = "to"),
            @Mapping(source = "dto.amount", target = "amount"),
            @Mapping(target = "createdAt", ignore = true)
    })
    Transaction transactionDtoToTransaction (TransactionDto dto, Card from, Card to);
}
