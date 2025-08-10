package com.example.bankcards.entity.converter;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import java.sql.Date;
import java.time.Instant;
import java.time.YearMonth;
import java.time.ZoneId;
import java.util.Optional;

@Converter(autoApply = true)
public class YearMonthDateConverter implements AttributeConverter<YearMonth, Date> {
    @Override
    public Date convertToDatabaseColumn(YearMonth attribute) {
        return Optional.ofNullable(attribute)
                .map(value -> Date.valueOf(attribute.atDay(1))).orElse(null);
    }

    @Override
    public YearMonth convertToEntityAttribute(Date data) {
        return Optional.ofNullable(data).map(value -> YearMonth.from(Instant
                .ofEpochMilli(value.getTime())
                .atZone(ZoneId.systemDefault())
                .toLocalDate())).orElse(null);
    }
}
