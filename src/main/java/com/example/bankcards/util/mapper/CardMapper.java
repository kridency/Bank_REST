package com.example.bankcards.util.mapper;

import com.example.bankcards.dto.CardDto;
import com.example.bankcards.entity.Card;
import org.mapstruct.*;

import java.util.Optional;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = "spring",
        uses = { RoleMapper.class })
@Named("CardMapper")
public interface CardMapper {
    @Mappings({
            @Mapping(source = "pan", target = "pan", qualifiedByName = "maskPan"),
            @Mapping(source = "expireDate", target = "expireDate"),
            @Mapping(source = "owner.email", target = "email"),
            @Mapping(source = "status", target = "status"),
            @Mapping(source = "balance", target = "balance")
    })
    CardDto cardToCardDto(Card card);

    @Named("maskPan")
    default String maskPan(String pan) {
        return Optional.ofNullable(pan)
                .map(value ->
                        value.substring(0, value.length() - 4).replaceAll("\\d", "*")
                                + value.substring(value.length() - 4)
                )
                .orElse(null);
    }
}
