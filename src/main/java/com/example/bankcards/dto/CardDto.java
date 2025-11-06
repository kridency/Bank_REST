package com.example.bankcards.dto;

import com.example.bankcards.entity.StatusType;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.YearMonth;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Data transfer object with banking card details.")
public class CardDto {
        @Pattern(regexp = "^([\\d]{4}( |$)){4}$", message = "Invalid banking card number.")
    @NotNull(message = "No value specified for the field PAN.")
    @Schema(description = "Primary Account Number.")
    private String pan;
    @Future
    @NotNull(message = "No value specified for the field expire_date.")
    @Schema(description = "Banking card expiration date.")
    @JsonProperty("expire_date")
    private YearMonth expireDate;
    @NotNull(message = "No value specified for the field email.")
    @Email(message = "Invalid value for the filed email.")
    @Schema(description = "Email of banking card holder.")
    private String email;
    @Schema(description = "Banking card current state.")
    private StatusType status;
    @Schema(description = "Banking card balance.")
    private BigDecimal balance;
}
