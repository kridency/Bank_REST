package com.example.bankcards.entity;

import com.example.bankcards.entity.converter.PanMaskConverter;
import com.example.bankcards.entity.converter.YearMonthDateConverter;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.JdbcType;
import org.hibernate.dialect.PostgreSQLEnumJdbcType;

import java.math.BigDecimal;
import java.time.YearMonth;
import java.util.UUID;

@Entity
@Table(name = "card")
@NamedEntityGraph(name = "user", attributeNodes = {@NamedAttributeNode("owner")})
@AllArgsConstructor
@NoArgsConstructor
@Getter @Setter
public class Card {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id")
    private UUID id;
    @Convert(converter = PanMaskConverter.class)
    @Column(name = "pan", nullable = false, unique = true)
    private String pan;
    @Column(name = "expire_date", columnDefinition = "date")
    @Convert(converter = YearMonthDateConverter.class)
    private YearMonth expireDate;
    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @PrimaryKeyJoinColumn(name = "owner_id", referencedColumnName = "id")
    private User owner;
    @Column(name = "status", nullable = false)
    @JdbcType(PostgreSQLEnumJdbcType.class)
    private StatusType status;
    @Column(name = "balance")
    private BigDecimal balance;
}
