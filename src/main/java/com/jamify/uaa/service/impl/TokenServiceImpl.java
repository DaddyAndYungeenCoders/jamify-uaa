package com.jamify.uaa.service.impl;

import com.jamify.uaa.service.TokenService;
import org.springframework.http.HttpHeaders;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.core.endpoint.OAuth2AccessTokenResponse;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.reactive.function.client.WebClient;

import java.nio.charset.StandardCharsets;
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
        OAuth2AuthorizedClient client = authorizedClientService.loadAuthorizedClient(provider, email);

        if (client == null || client.getRefreshToken() == null) {
            return Map.of("error", "Refresh token not found for the user");
        }

        OAuth2AccessTokenResponse response = WebClient.builder()
                .build()
                .post()
                .uri(client.getClientRegistration().getProviderDetails().getTokenUri())
                .header(HttpHeaders.AUTHORIZATION, "Basic " + encodeCredentials(client.getClientRegistration()))
                .bodyValue(new LinkedMultiValueMap<>(Map.of(
                        "grant_type", List.of("refresh_token"),
                        "refresh_token", List.of(client.getRefreshToken().getTokenValue())
                )))
                .retrieve()
                .bodyToMono(OAuth2AccessTokenResponse.class)
                .block();

        if (response == null) {
            return Map.of("error", "Failed to refresh the token");
        }

        OAuth2AuthorizedClient updatedClient = new OAuth2AuthorizedClient(
                client.getClientRegistration(),
                client.getPrincipalName(),
                response.getAccessToken(),
                response.getRefreshToken()
        );
        authorizedClientService.saveAuthorizedClient(updatedClient, null);

        return Map.of("access_token", response.getAccessToken().getTokenValue());
    }

    private String encodeCredentials(ClientRegistration registration) {
        String credentials = registration.getClientId() + ":" + registration.getClientSecret();
        return Base64.getEncoder().encodeToString(credentials.getBytes(StandardCharsets.UTF_8));
    }
}