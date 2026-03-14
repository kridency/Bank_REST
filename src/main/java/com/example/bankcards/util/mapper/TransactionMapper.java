package com.example.bankcards.util.mapper;

import com.example.bankcards.dto.CardDto;
import com.example.bankcards.dto.TransactionDto;
import com.example.bankcards.entity.Transaction;
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
}
