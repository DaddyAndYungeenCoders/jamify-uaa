package com.jamify.uaa.config.service;

import com.jamify.uaa.domain.dto.UserDto;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Header;
import io.jsonwebtoken.Jwts;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;

import java.io.File;
import java.nio.file.Files;
import java.security.KeyFactory;
import java.security.interfaces.RSAPrivateKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Service class for handling JWT operations such as generating and validating tokens.
 */
@Slf4j
@Service
public class JwtService {

    @Value("${spring.security.oauth2.resourceserver.jwt.issuer-uri}")
    private String issuerUri;

    @Value("${security.jwt.private-key}")
    private String privateKeyPath;

    @Value("${security.jwt.jwk-key-id}")
    private String keyId;

    @Autowired
    private ResourceLoader resourceLoader;

    private RSAPrivateKey key;

    /**
     * Initializes the signing key for JWT.
     */
    @PostConstruct
    public void init() {
        try {
            Resource resource = resourceLoader.getResource(privateKeyPath);
            key = loadPrivateKey(resource.getFile());
        } catch (Exception e) {
            log.error("Error loading private key: {}", e.getMessage());
        }
    }

    /**
     * Generates a JWT token for the given user.
     *
     * @param user the user entity for which the token is generated
     * @return the generated JWT token
     */
    public String generateToken(UserDto user) {
        log.info("Generating token for user: {}", user.name());
        return Jwts.builder()
                .setHeaderParam(Header.TYPE, Header.JWT_TYPE)
                .setHeaderParam("kid", keyId)
                .setSubject(user.name())
                .setIssuer(issuerUri)
                .claim("email", user.email())
                .claim("roles", user.roles())
                .claim("country", user.country())
                .claim("provider", user.provider())
                .claim("id", user.userProviderId())
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
        log.debug("Validating token: {}", jwt);
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
    public String getUserEmailFromToken(String token) {
        log.debug("Getting username (user mail) from token: {}", token);
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .get("email", String.class);
    }

    public String getUserEmailFromExpiredToken(String token) {
        try {
            log.debug("Getting username (user mail) from possibly expired token: {}", token);

            return Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token)
                    .getBody()
                    .get("email", String.class);
        } catch (ExpiredJwtException e) {
            log.warn("Token has expired: {}", token);
            return e.getClaims().get("email", String.class);
        } catch (Exception e) {
            log.error("Error while parsing token: {}", token, e);
        }
        return null;
    }

    /**
     * Extracts the user id from the given JWT token.
     *
     * @param token the JWT token
     * @return the user id extracted from the token
     */
    public Long getUserIdFromToken(String token) {
        log.debug("Getting user id from token: {}", token);
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token)
                    .getBody()
                    .get("id", Long.class);
        } catch (ExpiredJwtException e) {
            log.error("Token is expired for user : {}", e.getClaims().get("id", Long.class));
            return e.getClaims().get("id", Long.class);
        } catch (Exception e) {
            log.error("Error getting user id from token: {}", e.getMessage());
            return null;
        }
    }

    /**
     * Extracts the roles from the given JWT token.
     *
     * @param token the JWT token
     * @return the list of roles extracted from the token
     */
    public Set<String> getRolesFromToken(String token) {
        log.debug("Getting roles from token: {}", token);
        List<?> roles = Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .get("roles", List.class);

        if (roles != null) {
            return roles.stream()
                    .map(Object::toString)
                    .collect(Collectors.toSet());
        }
        return Collections.emptySet();
    }

    private RSAPrivateKey loadPrivateKey(File file) throws Exception {
        String key = Files.readString(file.toPath());
        String privateKeyPEM = key
                .replace("-----BEGIN PRIVATE KEY-----", "")
                .replace("-----BEGIN RSA PRIVATE KEY-----", "")  // Support both formats
                .replace("-----END PRIVATE KEY-----", "")
                .replace("-----END RSA PRIVATE KEY-----", "")
                .replaceAll("\\s", "");

        byte[] encoded = Base64.getDecoder().decode(privateKeyPEM);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(encoded);
        return (RSAPrivateKey) keyFactory.generatePrivate(keySpec);
    }
}