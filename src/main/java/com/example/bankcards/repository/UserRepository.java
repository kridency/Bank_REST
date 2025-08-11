package com.example.bankcards.repository;

import com.example.bankcards.entity.User;
import org.hibernate.annotations.DynamicUpdate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
@DynamicUpdate
public interface UserRepository extends JpaRepository<User, UUID> {
    Optional<User> getByEmail(String email);
    int deleteByEmail(String email);
}
