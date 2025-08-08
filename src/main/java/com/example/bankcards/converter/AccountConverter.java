package com.example.bankcards.converter;

import com.example.bankcards.annotation.Mask;
import jakarta.annotation.Nonnull;
import org.springframework.core.convert.TypeDescriptor;
import org.springframework.core.convert.converter.ConditionalGenericConverter;
import org.springframework.core.convert.converter.GenericConverter;

import java.util.Collections;
import java.util.Set;

public class AccountConverter implements ConditionalGenericConverter {
    @Override
    public boolean matches(@Nonnull TypeDescriptor sourceType, @Nonnull TypeDescriptor targetType) {
        return sourceType.getAnnotation(Mask.class) != null;
    }

    @Override
    public Set<GenericConverter.ConvertiblePair> getConvertibleTypes() {
        return Collections.singleton(new GenericConverter.ConvertiblePair(String.class, String.class));
    }

    @Override
    public Object convert(Object source,@Nonnull TypeDescriptor sourceType,@Nonnull TypeDescriptor targetType) {
        // Conversion logic here
        // In this example it strips "value" from the source string
        if (source instanceof String value) {
            return  value.matches("[\\d{4}( |^$)]{4}") ? value.replaceAll(".(?=.{4})", "*") : value;
        } else {
            return source;
        }
    }
}
