package com.example.bankcards.entity.converter;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import lombok.extern.slf4j.Slf4j;

import java.util.Optional;

@Slf4j
@Converter
public class PanMaskConverter implements AttributeConverter<String, String> {
    @Override
    public String convertToDatabaseColumn(String attribute) {
        return Optional.ofNullable(attribute).filter(value ->
                value.replaceAll(" ","")
                .matches("^(?:4[\\d]{12}(?:[\\d]{3})?|5[1-5][\\d]{14}|3[47][\\d]{13})$")
        ).orElseGet(() -> { log.error("Номер карты не валиден!"); return attribute; });
    }

    @Override
    public String convertToEntityAttribute(String data) {
        return data;
    }
}
