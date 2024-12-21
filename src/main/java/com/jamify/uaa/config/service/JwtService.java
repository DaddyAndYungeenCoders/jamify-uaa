package com.jamify.uaa.config.service;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.List;

@Service
public class JwtService {
    private static final Logger log = LoggerFactory.getLogger(JwtService.class);
    private final Key key;


    public JwtService() {
        key = Keys.secretKeyFor(SignatureAlgorithm.HS256);
    }

    public String generateToken(CustomOAuth2User user) {
        log.info("Generating token for user: {}", user.getName());
        return Jwts.builder()
                .setSubject(user.getName())
                .claim("email", user.getAttribute("email"))
                .claim("id", user.getAttribute("id"))
                .claim("name", user.getAttribute("display_name"))
                .claim("country", user.getAttribute("country"))
                .claim("roles", List.of("ROLE_USER"))
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 86400000))
                .signWith(key)
                .compact();
    }

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

    public String getUsernameFromToken(String token) {
        log.info("Getting username from token: {}", token);
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .get("email", String.class);
    }

    public List<String> getRolesFromToken(String token) {
        log.info("Getting roles from token: {}", token);
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .get("roles", List.class);
    }
//
//    public Authentication getAuthentication(String jwt) {
//        try {
//
//        }
//    }
}