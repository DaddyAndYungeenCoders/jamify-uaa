package com.jamify.uaa.service.impl;

import com.jamify.uaa.domain.dto.ProviderAccessTokenResponse;
import com.jamify.uaa.exceptions.auth.custom.RefreshAccessTokenException;
import com.jamify.uaa.service.TokenService;
import org.springframework.http.HttpHeaders;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.core.OAuth2RefreshToken;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.reactive.function.client.WebClient;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Base64;
import java.util.List;
import java.util.Map;

@Service
public class TokenServiceImpl implements TokenService {

    private final OAuth2AuthorizedClientService authorizedClientService;

    public TokenServiceImpl(OAuth2AuthorizedClientService authorizedClientService) {
        this.authorizedClientService = authorizedClientService;
    }

    @Override
    public Map<String, String> refreshAccessToken(String provider, String email) {
//        User name = userService.getUserByEmail(email);

        OAuth2AuthorizedClient client = authorizedClientService.loadAuthorizedClient(provider, email);


        if (client == null || client.getRefreshToken() == null) {
            throw new RefreshAccessTokenException("Refresh token not found for the user");
        }

        ProviderAccessTokenResponse response = WebClient.builder()
                .build()
                .post()
                .uri(client.getClientRegistration().getProviderDetails().getTokenUri())
                .header(HttpHeaders.AUTHORIZATION, "Basic " + encodeCredentials(client.getClientRegistration()))
                .bodyValue(new LinkedMultiValueMap<>(Map.of(
                        "grant_type", List.of("refresh_token"),
                        "refresh_token", List.of(client.getRefreshToken().getTokenValue(),
                        "client_id", client.getClientRegistration().getClientId())
                )))
                .retrieve()
                .bodyToMono(ProviderAccessTokenResponse.class)
                .block();

        if (response == null) {
            return Map.of("error", "Failed to refresh the access token from " + provider);
        }

        // handle case when refresh token is not returned, it can happen
        OAuth2RefreshToken refreshToken = client.getRefreshToken();
        if (response.getRefreshToken() != null) {
            refreshToken = new OAuth2RefreshToken(response.getRefreshToken(), Instant.now());
        }

        OAuth2AuthorizedClient updatedClient = new OAuth2AuthorizedClient(
                client.getClientRegistration(),
                client.getPrincipalName(),
                buildOAuth2AccessTokenFromResponse(response),
                refreshToken
        );

        // todo: set authentication instead of null
        authorizedClientService.saveAuthorizedClient(updatedClient, null);
        return Map.of("access_token", response.getAccessToken());
    }

    private OAuth2AccessToken buildOAuth2AccessTokenFromResponse(ProviderAccessTokenResponse response) {
        return new OAuth2AccessToken(
                getTokenType(response.getTokenType()),
                response.getAccessToken(),
                Instant.now(),
                Instant.now().plusSeconds(Long.parseLong(response.getExpiresIn())),
                null
        );
    }

    private String encodeCredentials(ClientRegistration registration) {
        String credentials = registration.getClientId() + ":" + registration.getClientSecret();
        return Base64.getEncoder().encodeToString(credentials.getBytes(StandardCharsets.UTF_8));
    }

    private OAuth2AccessToken.TokenType getTokenType(String tokenType) {
        // unnecessary switch statement until more token types are supported
        switch (tokenType) {
            case "Bearer":
                return OAuth2AccessToken.TokenType.BEARER;
            default:
                // throw exception ?
                return OAuth2AccessToken.TokenType.BEARER;
        }
    }
}