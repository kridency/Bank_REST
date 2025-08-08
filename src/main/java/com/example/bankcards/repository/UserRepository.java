package com.example.bankcards.repository;

import com.example.bankcards.entity.User;
import org.hibernate.annotations.DynamicUpdate;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
@DynamicUpdate
public interface UserRepository extends CrudRepository<User, UUID> {
    Optional<User> getByEmail(String email);
    Optional<User> deleteByEmail(String email);
}
