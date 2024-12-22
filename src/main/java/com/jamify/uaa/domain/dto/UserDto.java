package com.jamify.uaa.domain.dto;

import java.util.List;

public record UserDto(
        String name,
        String email,
        String imgUrl,
        String role,
        List<String> playlists,
        List<String> events,
        List<String> jams,
        List<String> badges
) {}
