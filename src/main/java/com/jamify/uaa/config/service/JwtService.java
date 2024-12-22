package com.jamify.uaa.config.service;

import com.jamify.uaa.domain.model.UserEntity;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.List;

/**
 * Service class for handling JWT operations such as generating and validating tokens.
 */
@Slf4j
@Service
public class JwtService {
    private final Key key;

    /**
     * Constructor for JwtService.
     * Initializes the signing key for JWT.
     */
    public JwtService() {
        key = Keys.secretKeyFor(SignatureAlgorithm.HS256);
    }

    /**
     * Generates a JWT token for the given user.
     *
     * @param user the user entity for which the token is generated
     * @return the generated JWT token
     */
    public String generateToken(UserEntity user) {
        log.info("Generating token for user: {}", user.getName());
        return Jwts.builder()
                .setSubject(user.getName())
                .claim("email", user.getEmail())
                .claim("roles", List.of(user.getRole()))
                .claim("country", user.getCountry())
                .claim("provider", user.getProvider())
                .claim("id", user.getId())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 86400000))
                .signWith(key)
                .compact();
    }

    /**
     * Validates the given JWT token.
     *
     * @param jwt the JWT token to validate
     * @return true if the token is valid, false otherwise
     */
    public boolean validateToken(String jwt) {
        log.info("Validating token: {}", jwt);
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(jwt);
            return true;
        } catch (Exception e) {
            log.error("Invalid JWT token: {}", e.getMessage());
            return false;
        }
    }

    /**
     * Extracts the username (email) from the given JWT token.
     *
     * @param token the JWT token
     * @return the username (email) extracted from the token
     */
    public String getUsernameFromToken(String token) {
        log.info("Getting username from token: {}", token);
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .get("email", String.class);
    }

    /**
     * Extracts the roles from the given JWT token.
     *
     * @param token the JWT token
     * @return the list of roles extracted from the token
     */
    public List<String> getRolesFromToken(String token) {
        log.info("Getting roles from token: {}", token);
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .get("roles", List.class);
    }
}