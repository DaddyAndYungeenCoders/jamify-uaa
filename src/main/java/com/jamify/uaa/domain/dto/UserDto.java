package com.jamify.uaa.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(description = "User details")
public record UserDto(
        @Schema(description = "User ID", example = "123456")
        String name,
        @Schema(description = "User email", example = "user@example.com")
        String email,
        @Schema(description = "User image URL", example = "https://example.com/user.jpg")
        String imgUrl,
        @Schema(description = "User role", example = "ROLE_USER")
        String role,
        @Schema(description = "User playlists", example = "[\"playlist1\", \"playlist2\"]")
        List<String> playlists,
        @Schema(description = "User events", example = "[\"event1\", \"event2\"]")
        List<String> events,
        @Schema(description = "User jams", example = "[\"jam1\", \"jam2\"]")
        List<String> jams,
        @Schema(description = "User badges", example = "[\"badge1\", \"badge2\"]")
        List<String> badges
) {}
