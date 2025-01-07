package com.jamify.uaa.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.util.Set;

@Schema(description = "User details")
@Builder
public record UserDto(
        @Schema(description = "User ID", example = "123456")
        String name,
        @Schema(description = "User email", example = "user@example.com")
        String email,
        @Schema(description = "User image URL", example = "https://example.com/user.jpg")
        String imgUrl,
        @Schema(description = "User country", example = "US")
        String country,
        @Schema(description = "User provider", example = "google")
        String provider,
        @Schema(description = "User provider ID", example = "123456")
        String userProviderId,
        @Schema(description = "User roles", example = "[ROLE_USER, ROLE_ADMIN]")
        Set<String> roles
) {
}
