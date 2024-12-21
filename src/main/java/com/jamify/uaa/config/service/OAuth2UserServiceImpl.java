//package com.jamify.uaa.config.service;
//
//import com.jamify.uaa.service.UserService;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
//import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
//import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
//import org.springframework.security.oauth2.core.user.OAuth2User;
//import org.springframework.stereotype.Service;
//
//@Service
//public class OAuth2UserServiceImpl implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {
//
//    private static final Logger log = LoggerFactory.getLogger(OAuth2UserServiceImpl.class);
//
//    private final UserService userService;
//
//    public OAuth2UserServiceImpl(UserService userService) {
//        this.userService = userService;
//    }
//
//    @Override
//    public OAuth2User loadUser(OAuth2UserRequest userRequest) {
//        DefaultOAuth2UserService delegate = new DefaultOAuth2UserService();
//        OAuth2User oauth2User = delegate.loadUser(userRequest);
//
//        String registrationId = userRequest.getClientRegistration().getRegistrationId();
//        String email = null;
//        String firstName = null;
//        String lastName = null;
//        String name = null;
//        String country = null;
//        String id = null;
//        String imgUrl = null;
//        String provider = null;
//
//        if ("spotify".equals(registrationId)) {
//            email = oauth2User.getAttribute("email");
//            // display_name is the full name, so we split it into first and last name
////            firstName = oauth2User.getAttribute("display_name").split(" ")[0];
////            lastName = oauth2User.getAttribute("display_name").split(" ")[1];
//            name = oauth2User.getAttribute("display_name");
//            country = oauth2User.getAttribute("country");
//            id = oauth2User.getAttribute("id");
//            // TODO
////            imgUrl = oauth2User.getAttribute("images").get(0).get("url");
//            provider = "spotify";
//        } else if ("deezer".equals(registrationId)) {
//            // TODO
////            email = oauth2User.getAttribute("email");
////            name = oauth2User.getAttribute("name");
//        }
//
//        log.info("User {} ({}) logged in with {}", name, email, registrationId);
//
//        // Create user in the application if not already exists
//        userService.createUserIfNotExists(email, name, country, id, imgUrl, provider);
//
//        return oauth2User;
//    }
//}