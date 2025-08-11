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
@Schema(description = "Объект обмена данными о реквизитах банковской карты.")
public class CardDto {
    @Pattern(regexp = "^([\\d]{4}( |$)){4}$", message = "Недопустимый номер банковской карты.")
    @NotNull(message = "Не указано значение для поля pan.")
    @Schema(description = "Номер банковской карты.")
    private String pan;
    @Future
    @NotNull(message = "Не указано значение для поля expire_date.")
    @Schema(description = "Месяц истечения срока действия банковской карты.")
    @JsonProperty("expire_date")
    private YearMonth expireDate;
    @NotNull(message = "Не указано значение для поля email.")
    @Email(message = "Недопустимое значение для адреса электронной почты.")
    @Schema(description = "Адрес электронной почты держателя банковской карты.")
    private String email;
    @Schema(description = "Текущий статус банковской карты.")
    private StatusType status;
    @Schema(description = "Текущий остаток денежных средств на банковском счете.")
    private BigDecimal balance;
}
