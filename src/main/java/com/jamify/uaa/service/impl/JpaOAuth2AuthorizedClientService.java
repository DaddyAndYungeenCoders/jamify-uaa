package com.jamify.uaa.service.impl;

import com.jamify.uaa.domain.model.OAuth2AuthorizedClientEntity;
import com.jamify.uaa.repository.OAuth2AuthorizedClientRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.core.OAuth2RefreshToken;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class JpaOAuth2AuthorizedClientService implements OAuth2AuthorizedClientService {
    private final OAuth2AuthorizedClientRepository repository;
    private final ClientRegistrationRepository clientRegistrationRepository;

    @Override
    @Transactional(readOnly = true)
    public <T extends OAuth2AuthorizedClient> T loadAuthorizedClient(String clientRegistrationId, String principalName) {
        return repository.findFirstByClientRegistrationIdAndPrincipalNameOrderByIdDesc(clientRegistrationId, principalName)
                .map(entity -> {
                    ClientRegistration registration = clientRegistrationRepository.findByRegistrationId(clientRegistrationId);

                    OAuth2AccessToken accessToken = new OAuth2AccessToken(
                            OAuth2AccessToken.TokenType.BEARER,
                            entity.getAccessToken(),
                            Instant.now(),
                            entity.getAccessTokenExpiresAt()
                    );

                    OAuth2RefreshToken refreshToken = null;
                    if (entity.getRefreshToken() != null) {
                        refreshToken = new OAuth2RefreshToken(
                                entity.getRefreshToken(),
                                entity.getRefreshTokenExpiresAt()
                        );
                    }

                    return (T) new OAuth2AuthorizedClient(
                            registration,
                            principalName,
                            accessToken,
                            refreshToken
                    );
                })
                .orElse(null);
    }

    @Override
    @Transactional
    public void saveAuthorizedClient(OAuth2AuthorizedClient authorizedClient, Authentication principal) {
        // First check if the entity already exists
        OAuth2AuthorizedClientEntity entity = repository
                .findFirstByClientRegistrationIdAndPrincipalNameOrderByIdDesc(
                        authorizedClient.getClientRegistration().getRegistrationId(),
                        authorizedClient.getPrincipalName()
                )
                .orElse(new OAuth2AuthorizedClientEntity());

        // Update the entity
        entity.setClientRegistrationId(authorizedClient.getClientRegistration().getRegistrationId());
        entity.setPrincipalName(authorizedClient.getPrincipalName());
        entity.setAccessToken(authorizedClient.getAccessToken().getTokenValue());
        entity.setAccessTokenExpiresAt(authorizedClient.getAccessToken().getExpiresAt());
        entity.setTokenType(authorizedClient.getAccessToken().getTokenType().getValue());

        if (authorizedClient.getRefreshToken() != null) {
            entity.setRefreshToken(authorizedClient.getRefreshToken().getTokenValue());
            entity.setRefreshTokenExpiresAt(authorizedClient.getRefreshToken().getExpiresAt());
        }

        String scopes = String.join(",", authorizedClient.getAccessToken().getScopes());
        entity.setAccessTokenScopes(scopes);

        repository.save(entity);
    }

    @Override
    @Transactional
    public void removeAuthorizedClient(String clientRegistrationId, String principalName) {
        repository.deleteByClientRegistrationIdAndPrincipalName(clientRegistrationId, principalName);
    }
}