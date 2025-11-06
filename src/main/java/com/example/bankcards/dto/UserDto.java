package com.example.bankcards.dto;

import com.example.bankcards.entity.RoleType;
import jakarta.validation.constraints.Email;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Schema(description = "Request/feedback for user create/refresh details.")
public class UserDto {
    @NotNull(message = "No value specified for the field email.")
    @Email(message = "Invalid value for email.")
    @Schema(description = "User email.")
    private String email;
    @NotNull(message = "No value specified for the filed password.")
    @Schema(description = "User password.")
    private String password;
    @Schema(description = "User roles list.")
    private Set<RoleType> roles;
}
