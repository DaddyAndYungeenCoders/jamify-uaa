package com.jamify.uaa.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @GetMapping("/logout")
    public String logout() {
        return "Logged out";
    }

    @GetMapping("/login")
    public ResponseEntity<String> login() {
        // Cette URL sera automatiquement gérée par Spring Security OAuth2
        return ResponseEntity.ok("Login");
    }
}
