package com.jamify.uaa.config.service;

import com.jamify.uaa.constants.AllowedProviders;
import com.jamify.uaa.domain.dto.UserDto;
import com.jamify.uaa.domain.vm.UserAccessToken;
import com.jamify.uaa.service.UaaRefreshTokenService;
import com.jamify.uaa.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.io.IOException;
import java.time.ZonedDateTime;
import java.util.Objects;

/**
 * Custom authentication success handler to process OAuth2 login success events.
 */
@Slf4j
@Component
public class CustomAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    @Value("${gateway.url}")
    private String gatewayUrl;

    @Value("${gateway.service.front}")
    private String frontendService;

    @Autowired
    private WebClient jamifyEngineWebClient;

    private final JwtService jwtService;
    private final UserService userService;
    private final UaaRefreshTokenService refreshTokenService;
    private final OAuth2AuthorizedClientService authorizedClientService;

    /**
     * Constructor for CustomAuthenticationSuccessHandler.
     *
     * @param jwtService  the service to handle JWT operations
     * @param userService the service to handle user operations
     */
    public CustomAuthenticationSuccessHandler(JwtService jwtService, UserService userService, UaaRefreshTokenService refreshTokenService, OAuth2AuthorizedClientService authorizedClientService) {
        this.jwtService = jwtService;
        this.userService = userService;
        this.refreshTokenService = refreshTokenService;
        this.authorizedClientService = authorizedClientService;
    }

    /**
     * Handles successful authentication by processing the OAuth2 user details,
     * creating the user if not already exists, and redirecting with a JWT token.
     *
     * @param request        the HTTP request
     * @param response       the HTTP response
     * @param authentication the authentication object
     * @throws IOException if an I/O error occurs during redirection
     */
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException {
        CustomOAuth2User oauthUser = (CustomOAuth2User) authentication.getPrincipal();
        log.debug("OAuth2 user: {}", oauthUser);
        OAuth2AuthenticationToken oauthToken = (OAuth2AuthenticationToken) authentication;
        log.debug("OAuth2 token: {}", oauthToken);

        // Get the provider (authorizedClientRegistrationId) from the authentication
        String provider = oauthToken.getAuthorizedClientRegistrationId();

        // Verify that the provider is in the AllowedProviders enum
        try {
            AllowedProviders allowedProvider = AllowedProviders.valueOf(provider.toUpperCase());
            log.info("User {} ({}) logged in with {}", oauthUser.getName(), oauthUser.getEmail(), allowedProvider.getValue());

            // Handling different providers, if there are different actions to be taken
            switch (allowedProvider) {
                case SPOTIFY:
//                    userService.createUserIfNotExists(
//                            oauthUser.getEmail(),
//                            oauthUser.getName(),
//                            oauthUser.getCountry(),
//                            oauthUser.getId(),
//                            oauthUser.getImgUrl(),
//                            provider
//                    );
                    userService.sendLoggedUserToEngineForCreation(oauthUser, provider);
                    break;
                case DEEZER:
                    // Add Deezer specific logic here
                    break;
                case APPLE_MUSIC:
                    // Add Apple Music specific logic here
                    break;
                default:
                    log.warn("Unhandled provider: {}", provider);
            }
        } catch (IllegalArgumentException e) {
            log.warn("Unknown provider: {}", provider);
        }

        // Retrieve the OAuth2AuthorizedClient
        OAuth2AuthorizedClient authorizedClient = authorizedClientService.loadAuthorizedClient(
                oauthToken.getAuthorizedClientRegistrationId(),
                oauthToken.getName()
        );

        if (authorizedClient != null) {
            String accessToken = authorizedClient.getAccessToken().getTokenValue();
            log.debug("Access Token: {}", accessToken);
            // send access token to the jamify-engine microservice
            try {
                ZonedDateTime expiresAt = ZonedDateTime.parse(Objects.requireNonNull(authorizedClient.getAccessToken().getExpiresAt()).toString());
                UserAccessToken userAccessToken = buildUserAccessToken(provider, accessToken, oauthUser, expiresAt);

                // send the access token to the jamify-engine microservice so that it can be used to query the provider's API
                jamifyEngineWebClient
                        .post()
                        .uri("/auth/access-token")
                        .bodyValue(userAccessToken)
                        .retrieve()
                        .bodyToMono(void.class)
                        .block();
            } catch (Exception e) {
                log.error("Error while sending access token to Jamify Engine :", e);
            }
        } else {
            log.warn("No authorized client found for provider: {}", provider);
        }

        // Get user and generate JWT token + generate refresh token
        UserDto user = userService.getUserByEmail(oauthUser.getEmail());
        String token = jwtService.generateToken(user);
        refreshTokenService.createRefreshToken(user.email());


        // Redirect to the frontend with the generated jwt
        String redirectUrl = gatewayUrl + frontendService + "/?token=" + token;
        response.sendRedirect(redirectUrl);
    }

    private UserAccessToken buildUserAccessToken(String provider, String accessToken, CustomOAuth2User oauthUser, ZonedDateTime expiresAt) {
        UserAccessToken userToken = new UserAccessToken();
        userToken.setProvider(provider);
        userToken.setAccessToken(accessToken);
        userToken.setEmail(oauthUser.getEmail());
        userToken.setExpiresAt(expiresAt.toLocalDateTime());
        return userToken;
    }
}