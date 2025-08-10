package com.example.bankcards.repository;

import com.example.bankcards.entity.Card;
import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface CardRepository extends PagingAndSortingRepository<Card, UUID>, JpaRepository<Card, UUID>, JpaSpecificationExecutor<Card> {
    Optional<Card> findByPan(String cardNumber);
    @EntityGraph(attributePaths = {"owner"})
    @Nonnull Page<Card> findAll(@Nullable Specification<Card> spec, @Nullable Pageable pageable);
}
