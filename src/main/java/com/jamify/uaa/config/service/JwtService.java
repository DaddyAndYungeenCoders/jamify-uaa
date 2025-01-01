package com.jamify.uaa.config.service;

import com.jamify.uaa.domain.model.UserEntity;
import com.jamify.uaa.service.UaaRefreshTokenService;
import com.jamify.uaa.service.UserService;
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
import java.util.Base64;
import java.util.Collections;
import java.util.Date;
import java.util.List;
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
    public String generateToken(UserEntity user) {
        log.info("Generating token for user: {}", user.getName());
        return Jwts.builder()
                .setHeaderParam(Header.TYPE, Header.JWT_TYPE)
                .setHeaderParam("kid", keyId)
                .setSubject(user.getName())
                .setIssuer(issuerUri)
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
    public String getUsernameFromToken(String token) {
        log.debug("Getting username from token: {}", token);
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .get("email", String.class);
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
    public List<String> getRolesFromToken(String token) {
        log.debug("Getting roles from token: {}", token);
        List<?> roles = Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .get("roles", List.class);

        if (roles != null) {
            return roles.stream()
                    .filter(role -> role instanceof String)
                    .map(role -> (String) role)
                    .collect(Collectors.toList());
        }
        return Collections.emptyList();
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