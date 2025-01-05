package com.jamify.uaa.controller;

import com.jamify.uaa.config.service.JwtService;
import com.jamify.uaa.domain.model.UaaRefreshToken;
import com.jamify.uaa.exceptions.auth.custom.InvalidApiKeyException;
import com.jamify.uaa.service.TokenService;
import com.jamify.uaa.service.UaaRefreshTokenService;
import com.jamify.uaa.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    @Value("${config.jamify-engine.api-key}")
    private String jamifyEngineApiKey;

    private final TokenService tokenService;
    private final JwtService jwtService;
    private final UaaRefreshTokenService refreshTokenService;
    private final UserService userService;

    public AuthController(TokenService tokenService, JwtService jwtService, UaaRefreshTokenService refreshTokenService, UserService userService) {
        this.tokenService = tokenService;
        this.jwtService = jwtService;
        this.refreshTokenService = refreshTokenService;
        this.userService = userService;
    }

    // Jamify engine will call this endpoint if the access token is expired
    // it needs the X-API-KEY header to be set to the value of the API key
    @Operation(summary = "Refresh access token",
            description = "Endpoint to refresh the access token for the given provider and email. Only authorized to Jamify Engine.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Access token refreshed successfully."),
                    @ApiResponse(responseCode = "401", description = "Unauthorized.")
            })
    @PostMapping("/refresh-access-token")
    public ResponseEntity<Map<String, String>> refreshAccessToken(
            @RequestHeader(value = "X-API-KEY") String apiKey,
            @RequestParam String provider,
            @RequestParam String email
    ) {
        if (!apiKey.equals(jamifyEngineApiKey)) {
            throw new InvalidApiKeyException("Invalid API key");
        }

        Map<String, String> tokenResponse = tokenService.refreshAccessToken(provider, email);
        return ResponseEntity.ok(tokenResponse);
    }

    @Operation(summary = "Refresh JWT token",
            description = "Endpoint to refresh the JWT token.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Token refreshed successfully."),
                    @ApiResponse(responseCode = "401", description = "Unauthorized.")
            })
    @PostMapping("/refresh-jwt-token")
    public ResponseEntity<Map<String, String>> refreshToken(@RequestHeader("Authorization") String token) {

        // Check if the token is valid
        if (token == null || !token.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        // Extract the token from the Authorization header
        String expiredToken = token.substring(7);
        // Extract the user ID from the token
        Long userId = jwtService.getUserIdFromToken(expiredToken);
        // Get the refresh token from the database
        UaaRefreshToken refreshToken = refreshTokenService.getTokenByUser(userService.getUserById(userId));

        // Check if the refresh token is valid, if so, generate a new JWT token
        if (refreshToken != null && !refreshTokenService.isRefreshTokenExpired(refreshToken)) {
            String newToken = jwtService.generateToken(userService.getUserById(userId));
            return ResponseEntity.ok(Map.of("token", newToken));
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }
}
