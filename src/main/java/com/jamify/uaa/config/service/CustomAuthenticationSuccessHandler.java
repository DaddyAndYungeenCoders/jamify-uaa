package com.jamify.uaa.config.service;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import java.io.IOException;

public class CustomAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    private final JwtService jwtService;

    public CustomAuthenticationSuccessHandler(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException {
        CustomOAuth2User oauthUser = (CustomOAuth2User) authentication.getPrincipal();
        String token = jwtService.generateToken(oauthUser);

        // Rediriger vers le frontend avec le token
        String redirectUrl = "http://localhost:5173/oauth/callback?token=" + token;
        response.sendRedirect(redirectUrl);
    }
}