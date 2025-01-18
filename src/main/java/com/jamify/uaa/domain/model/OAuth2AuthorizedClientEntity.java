package com.jamify.uaa.domain.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.Instant;

@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "oauth2_authorized_clients")
@Data
public class OAuth2AuthorizedClientEntity extends AbstractEntity<Long> {

    private String clientRegistrationId;
    private String principalName;

    @Column(length = 4000)
    private String accessToken;

    @Column(length = 4000)
    private String refreshToken;

    private Instant accessTokenExpiresAt;
    private Instant refreshTokenExpiresAt;

    @Column(length = 1000)
    private String accessTokenScopes;

    @Column(length = 255)
    private String tokenType;
}