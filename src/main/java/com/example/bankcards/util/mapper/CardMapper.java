package com.example.bankcards.util.mapper;

import com.example.bankcards.dto.CardDto;
import com.example.bankcards.entity.Card;
import com.example.bankcards.entity.User;
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
    CardDto cardToCardDto (Card card);

    @Mappings({
            @Mapping(target = "id", ignore = true),
            @Mapping(source = "dto.pan", target = "pan"),
            @Mapping(source = "dto.expireDate", target = "expireDate"),
            @Mapping(source = "owner", target = "owner"),
            @Mapping(source = "dto.status", target = "status"),
            @Mapping(source = "dto.balance", target = "balance")
    })
    Card cardDtoToCard (CardDto dto, User owner);

    void updateEntityFromDto (CardDto dto, @MappingTarget Card.CardBuilder card);

    @Named("maskPan")
    default String maskPan (String pan) {
        return Optional.ofNullable(pan).filter(x -> x.length() > 4).map(value ->
                        value.substring(0, value.length() - 4).replaceAll("\\d", "*")
                                + value.substring(value.length() - 4)).orElse(pan);
    }
}
