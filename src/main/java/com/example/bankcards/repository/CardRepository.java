package com.example.bankcards.repository;

import com.example.bankcards.entity.Card;
import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface CardRepository extends PagingAndSortingRepository<Card, UUID>, JpaRepository<Card, UUID>, JpaSpecificationExecutor<Card> {
    Optional<Card> findByPan(String pan);
    @EntityGraph(attributePaths = {"owner"})
    @Nonnull
    Page<Card> findAll(@Nullable Specification<Card> spec, @Nullable Pageable pageable);
    int deleteByPan(String pan);
    @Modifying
    @Transactional
    @Query("UPDATE Card t SET t.status = 'EXPIRED' WHERE t.expireDate < :now AND t.status = 'ACTIVE'")
    void updateExpiredCards(@Param("now") Instant now);
}
