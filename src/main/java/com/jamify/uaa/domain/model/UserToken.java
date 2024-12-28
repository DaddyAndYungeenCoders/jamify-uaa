package com.jamify.uaa.domain.model;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class UserToken {
    private String email;
    private String provider;
    private String accessToken;
    private LocalDateTime expiresAt;
}

