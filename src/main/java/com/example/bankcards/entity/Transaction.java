package com.example.bankcards.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.DynamicInsert;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Entity
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
@Table(name = "transaction")
@Getter
@Builder(toBuilder = true)
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
    @Column(name = "created_at", insertable = false, updatable = false)
    private Instant createdAt;
}
