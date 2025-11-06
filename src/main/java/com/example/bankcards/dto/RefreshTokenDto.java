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
@Schema(description = "Feedback for authentication/token refresh request.")
public class RefreshTokenDto {
    @JsonProperty("access_token")
    @Schema(description = "Token characters sequence.")
    private String accessToken;
}
