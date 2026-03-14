package com.example.bankcards.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Getter
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "transaction")
public class Transaction implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    @ManyToOne(fetch = FetchType.EAGER)
    @PrimaryKeyJoinColumn(name = "from_pan", referencedColumnName = "id")
    private Card from;
    @ManyToOne(fetch = FetchType.EAGER)
    @PrimaryKeyJoinColumn(name = "to_pan", referencedColumnName = "id")
    private Card to;
    @Column(name = "amount", columnDefinition = "decimal")
    private BigDecimal amount;
    @CreatedDate
    @Column(name = "created_at")
    private Instant createdAt;
}
