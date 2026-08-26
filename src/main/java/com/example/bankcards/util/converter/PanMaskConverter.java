package com.example.bankcards.util.converter;

import com.example.bankcards.service.CryptService;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.util.Optional;

@Slf4j
@Converter
public class PanMaskConverter implements AttributeConverter<String, String> {
    private final CryptService cryptService;

    public PanMaskConverter(CryptService cryptService) {
        this.cryptService = cryptService;
    }

    @SneakyThrows
    @Override
    public String convertToDatabaseColumn(String attribute) {
        return Optional.ofNullable(attribute).filter(value ->
                value.replaceAll(" ","")
                .matches("^(?:4[\\d]{12}(?:[\\d]{3})?|5[1-5][\\d]{14}|3[47][\\d]{13})$"))
                .map(value -> {
                    try {
                        return cryptService.encrypt(value);
                    } catch (Throwable e) {
                        throw new RuntimeException(e.getMessage());
                    }
                })
                .orElseGet(() -> { log.error("Non valid card number!"); return attribute; });
    }

    @SneakyThrows
    @Override
    public String convertToEntityAttribute(String data) {
        return cryptService.decrypt(data);
    }
}
