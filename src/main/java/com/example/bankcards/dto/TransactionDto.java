package com.example.bankcards.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TransactionDto {
    @Schema(description = "Source Primary Account Number.")
    private String from;
    @Schema(description = "Destination Primary Account Number.")
    private String to;
    @Schema(description = "Transferred amount.")
    private BigDecimal amount;
    @Schema(description = "Transfer moment.")
    private Instant createdAt;
}
