package com.jamify.uaa.config.service;

import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

/**
 * Custom OAuth2 user service that extends the default OAuth2 user service.
 * This service is used to load user details from the OAuth2 provider.
 */
@Service
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    /**
     * Loads the user from the OAuth2 user request and wraps it in a CustomOAuth2User.
     * This method is called by the Spring Security framework during the OAuth2 login process.
     *
     * @param userRequest the OAuth2 user request containing the access token and client registration
     * @return the OAuth2 user with additional custom attributes
     * @throws OAuth2AuthenticationException if an error occurs while loading the user
     */
    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        // Load the user from the OAuth2 provider using the default implementation
        OAuth2User oauth2User = super.loadUser(userRequest);
        // Wrap the loaded user in a CustomOAuth2User to add custom attributes
        return new CustomOAuth2User(oauth2User);
    }
}