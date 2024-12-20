package com.jamify.uaa.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @GetMapping("/test")
    @PreAuthorize("hasRole('USER')")
    public String protectedEndpoint(@AuthenticationPrincipal OAuth2User principal) {
        if (principal == null) {
            return "Not authenticated: principal is null";
        }
        return "Auth successful for " + principal.getAttribute("display_name") + " (" + principal.getAttribute("email") + ")";
    }

    @GetMapping("/logout")
    public String logout() {
        return "Logged out";
    }

    @GetMapping("/test1")
    @PreAuthorize("hasRole('USER')")
    public String test() {
        return "You can see this because you are logged in";
    }

    @GetMapping("/")
    public String home() {
        return "Welcome to Jamify!";
    }

}
