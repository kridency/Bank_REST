package com.example.bankcards.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "refresh_token")
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class RefreshToken {
    @Id
    private UUID id;
    @Column(name = "token", nullable = false, columnDefinition = "text")
    private String token;
    @Column(name = "issue_date", nullable = false)
    private Instant issueDate;
    @Column(name = "expire_date", nullable = false)
    private Instant expireDate;
}
