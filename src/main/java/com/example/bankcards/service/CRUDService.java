package com.example.bankcards.service;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

import java.util.Map;

public interface CRUDService<T> {
    Slice<T> get(Map<String, ? extends Comparable<?>> criteria, Pageable pageable);
    T create(T dto);
    T update(T dto);
    int delete(T dto);
}
