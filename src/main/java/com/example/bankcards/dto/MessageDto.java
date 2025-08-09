package com.example.bankcards.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Сообщение о завершенной операции.")
public class MessageDto {
    @Schema(description = "Текст сообщения.")
    private String message;
    private String description;
}
