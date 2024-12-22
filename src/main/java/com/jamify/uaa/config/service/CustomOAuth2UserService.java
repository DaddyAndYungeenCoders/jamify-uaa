package com.jamify.uaa.config.service;

import com.jamify.uaa.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

@Service
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private static final Logger log = LoggerFactory.getLogger(CustomOAuth2UserService.class);
    private final UserService userService;

    public CustomOAuth2UserService(UserService userService) {
        this.userService = userService;
    }

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oauth2User = super.loadUser(userRequest);

//        String registrationId = userRequest.getClientRegistration().getRegistrationId();
//        String email = null;
//        String name = null;
//        String country = null;
//        String id = null;
//        String imgUrl = null;
//        String provider = null;
//
//        if ("spotify".equals(registrationId)) {
//            email = oauth2User.getAttribute("email");
//            name = oauth2User.getAttribute("display_name");
//            country = oauth2User.getAttribute("country");
//            id = oauth2User.getAttribute("id");
//            provider = "spotify";
//        } else if ("deezer".equals(registrationId)) {
//            // TODO: Handle Deezer attributes
//        }
//
//        log.info("User {} ({}) logged in with {}", name, email, registrationId);
//
//        // Create user in the application if not already exists
//        userService.createUserIfNotExists(email, name, country, id, imgUrl, provider);

        return new CustomOAuth2User(oauth2User);
    }
}