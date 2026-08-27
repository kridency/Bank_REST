package com.example.bankcards.repository;

import com.example.bankcards.entity.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

@Repository
public interface TokenRepository extends JpaRepository<RefreshToken, UUID> {
    @Modifying
    @Transactional
    @Query("DELETE FROM RefreshToken t WHERE t.expireDate < :now")
    void deleteExpiredTokens(@Param("now") Instant now);
}
