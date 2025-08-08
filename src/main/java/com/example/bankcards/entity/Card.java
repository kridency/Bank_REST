package com.example.bankcards.entity;

import com.example.bankcards.annotation.Mask;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.YearMonth;
import java.util.UUID;

@Entity
@Table(name = "card")
public class Card {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    @Mask
    @Column(name = "account", nullable = false)
    private String account;
    @Temporal(TemporalType.DATE)
    @Column(name = "expiry_date", nullable = false)
    private YearMonth expiryDate;
    @ManyToOne(fetch = FetchType.EAGER)
    @PrimaryKeyJoinColumn(name = "owner", referencedColumnName = "id")
    private User owner;
    @Enumerated(EnumType.STRING)
    private StatusType status;
    private BigDecimal balance;
}
