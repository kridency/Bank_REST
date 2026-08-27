package com.example.bankcards.repository;

import com.example.bankcards.entity.User;
import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import org.hibernate.annotations.DynamicUpdate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
@DynamicUpdate
public interface UserRepository extends PagingAndSortingRepository<User, UUID>, JpaRepository<User, UUID> {
    Optional<User> getByEmail(String email);
    @Nonnull
    Page<User> findAll(@Nullable Specification<User> spec, @Nullable Pageable pageable);
    int deleteByEmail(String email);
}
