package com.jamify.uaa.config.service;

import com.jamify.uaa.domain.model.UserEntity;
import com.jamify.uaa.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import java.io.IOException;

/**
 * Custom authentication success handler to process OAuth2 login success events.
 */
@Slf4j
public class CustomAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    private final JwtService jwtService;
    private final UserService userService;

    /**
     * Constructor for CustomAuthenticationSuccessHandler.
     *
     * @param jwtService  the service to handle JWT operations
     * @param userService the service to handle user operations
     */
    public CustomAuthenticationSuccessHandler(JwtService jwtService, UserService userService) {
        this.jwtService = jwtService;
        this.userService = userService;
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

        // Get the provider (authorizedClientRegistrationId) from the authentication
        String provider = ((OAuth2AuthenticationToken) authentication).getAuthorizedClientRegistrationId();

        // Handling different providers
        switch (provider) {
            case "spotify":
                log.info("User {} ({}) logged in with Spotify", oauthUser.getName(), oauthUser.getEmail());
                break;
            case "deezer":
                log.info("User {} ({}) logged in with Deezer", oauthUser.getName(), oauthUser.getEmail());
                break;
            default:
                log.warn("Unknown provider: {}", provider);
        }

        // Create user in the application if not already exists
        userService.createUserIfNotExists(
                oauthUser.getEmail(),
                oauthUser.getName(),
                oauthUser.getCountry(),
                oauthUser.getId(),
                oauthUser.getImgUrl(),
                provider
        );

        UserEntity user = userService.getUserByEmail(oauthUser.getEmail());
        String token = jwtService.generateToken(user);

        // Redirect to the frontend with the token
        String redirectUrl = "http://localhost:5173/oauth/callback?token=" + token;
        response.sendRedirect(redirectUrl);
    }
}