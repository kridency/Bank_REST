package com.example.bankcards.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Schema(description = "Ответ по запросу на аутентификацию/обновление электронного пропуска.")
public class TokenDto {
    @JsonProperty("access_token")
    @Schema(description = "Символьная последовательность электронного пропуска.")
    private String accessToken;
}
