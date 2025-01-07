package com.jamify.uaa.config.service;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Collection;
import java.util.List;
import java.util.Map;

public class CustomOAuth2User implements OAuth2User {
    private final OAuth2User oauth2User;

    public OAuth2User getOauth2User() {
        return oauth2User;
    }

    public CustomOAuth2User(OAuth2User oauth2User) {
        this.oauth2User = oauth2User;
    }

    @Override
    public Map<String, Object> getAttributes() {
        return oauth2User.getAttributes();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return oauth2User.getAuthorities();
    }

    @Override
    public String getName() {
        // handle different providers
        if (oauth2User.getAttribute("display_name") != null) {
            return oauth2User.getAttribute("display_name");
        } else if (oauth2User.getAttribute("name") != null) {
            return oauth2User.getAttribute("name");
        } else {
            return oauth2User.getAttribute("email");
        }
    }

    public String getEmail() {
        return oauth2User.getAttribute("email");
    }

    public String getCountry() {
        return oauth2User.getAttribute("country");
    }

    public String getId() {
        if (oauth2User.getAttribute("id") != null) {
            return oauth2User.getAttribute("id");
        } else if (oauth2User.getAttribute("user_id") != null) {
            return oauth2User.getAttribute("user_id");
        } else {
            return oauth2User.getAttribute("sub");
        }
    }

    public String getImgUrl() {
        List<Map<String, Object>> images = oauth2User.getAttribute("images");
        if (images != null && !images.isEmpty()) {
            return (String) images.getFirst().get("url");
        }
        return null;
    }

    // TODO: how do we handle multiple provider with different attributes?
}