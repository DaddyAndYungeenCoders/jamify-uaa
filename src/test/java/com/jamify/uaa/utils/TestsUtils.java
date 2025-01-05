package com.jamify.uaa.utils;

import com.jamify.uaa.domain.model.UaaRefreshToken;
import com.jamify.uaa.domain.model.UserEntity;
import io.jsonwebtoken.Header;
import io.jsonwebtoken.Jwts;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.io.File;
import java.nio.file.Files;
import java.security.KeyFactory;
import java.security.interfaces.RSAPrivateKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.time.Instant;
import java.util.Base64;
import java.util.Date;
import java.util.List;

@Slf4j
@Component
public class TestsUtils {

    private static final String ISSUER_URI = "https://test-issuer.com";
    private static final String KEY_ID = "test-key-id";
    private static final String TEST_USER_EMAIL = "test-user@example.com";
    private static final String TEST_PROVIDER = "spotify";

    @Value("${security.jwt.private-key}")
    private String privateKeyPath;

    @Autowired
    private ResourceLoader resourceLoader;

    private static RSAPrivateKey key;

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
     * Génère un JWT valide pour des tests.
     *
     * @return Un JWT valide.
     */
    public static String buildValidJwt(UserEntity user) {
        return Jwts.builder()
                .setHeaderParam(Header.TYPE, Header.JWT_TYPE)
                .setHeaderParam("kid", KEY_ID)
                .setSubject(user.getEmail())
                .setIssuer(ISSUER_URI)
                .claim("email", user.getEmail())
                .claim("roles", List.of(user.getRole()))
                .claim("country", user.getCountry())
                .claim("provider", user.getProvider())
                .claim("id", user.getId())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 3600000)) // Expirera dans 1 heure
                .signWith(key)
                .compact();
    }

    /**
     * Génère un JWT expiré pour des tests.
     *
     * @return Un JWT expiré.
     */
    public static String buildExpiredJwt(UserEntity user) {
        return Jwts.builder()
                .setHeaderParam(Header.TYPE, Header.JWT_TYPE)
                .setHeaderParam("kid", KEY_ID)
                .setSubject(user.getEmail())
                .setIssuer(ISSUER_URI)
                .claim("email", user.getEmail())
                .claim("roles", List.of(user.getRole()))
                .claim("country", user.getCountry())
                .claim("provider", user.getProvider())
                .claim("id", user.getId())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() - 3600000)) // Expiré depuis 1 heure
                .signWith(key)
                .compact();
    }

    public static UserEntity buildUserEntity() {
        UserEntity user = new UserEntity();
        user.setId(1L);
        user.setName("Test User");
        user.setEmail(TEST_USER_EMAIL);
        user.setRole("ROLE_USER");
        user.setCountry("Testland");
        user.setProvider("test-provider");
        return user;
    }

    public static UaaRefreshToken buildValidRefreshToken(UserEntity mockUser) {
        UaaRefreshToken refreshToken = new UaaRefreshToken();
        refreshToken.setId(1L);
        refreshToken.setUser(mockUser);
        refreshToken.setExpiryDate(Instant.now().plusMillis(3600000)); // Expirera dans 1 heure
        return refreshToken;
    }

    public static MultiValueMap<String, String> buildRefreshAccessTokenParams() {
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("provider", TEST_PROVIDER);
        params.add("email", TEST_USER_EMAIL);
        return params;
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
