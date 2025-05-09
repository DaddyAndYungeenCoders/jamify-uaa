package com.jamify.uaa.utils;

import com.jamify.uaa.domain.dto.UserDto;
import com.jamify.uaa.domain.model.UaaRefreshToken;
import io.jsonwebtoken.Header;
import io.jsonwebtoken.Jwts;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import mockwebserver3.MockResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.core.OAuth2RefreshToken;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.io.File;
import java.nio.file.Files;
import java.security.KeyFactory;
import java.security.interfaces.RSAPrivateKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.time.Instant;
import java.util.*;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@Slf4j
@Component
public class TestsUtils {

    private static final String ISSUER_URI = "https://test-issuer.com";
    private static final String KEY_ID = "test-key-id";
    private static final String TEST_USER_EMAIL = "test-user@example.com";
    private static final String TEST_EXPIRED_USER_EMAIL = "test-expired-user@example.com";
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
    public static String buildValidJwt(UserDto user) {
        return Jwts.builder()
                .setHeaderParam(Header.TYPE, Header.JWT_TYPE)
                .setHeaderParam("kid", KEY_ID)
                .setSubject(user.email())
                .setIssuer(ISSUER_URI)
                .claim("email", user.email())
                .claim("roles", user.roles())
                .claim("country", user.country())
                .claim("provider", user.provider())
                .claim("id", user.userProviderId())
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
    public static String buildExpiredJwt(UserDto user) {
        return Jwts.builder()
                .setHeaderParam(Header.TYPE, Header.JWT_TYPE)
                .setHeaderParam("kid", KEY_ID)
                .setSubject(user.email())
                .setIssuer(ISSUER_URI)
                .claim("email", user.email())
                .claim("roles", List.of(user.roles()))
                .claim("country", user.country())
                .claim("provider", user.provider())
                .claim("id", user.userProviderId())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() - 3600000)) // Expiré depuis 1 heure
                .signWith(key)
                .compact();
    }

    public static UserDto buildUserDto() {
        return new UserDto(
                "Test User",
                TEST_USER_EMAIL,
                "img.png",
                "FR",
                "test-provider",
                "11111111",
                Set.of("ROLE_USER"),
                Collections.emptySet()
        );
    }

    public static UserDto buildUserDtoWithExpiredRefreshToken() {
        return new UserDto(
                "Test Expired User",
                TEST_EXPIRED_USER_EMAIL,
                "img.png",
                "FR",
                "test-provider",
                "11111111",
                Set.of("ROLE_USER"),
                Collections.emptySet()
        );
    }

    public static UaaRefreshToken buildValidRefreshToken(UserDto mockUser) {
        UaaRefreshToken refreshToken = new UaaRefreshToken();
        refreshToken.setId(1L);
        refreshToken.setUserEmail(mockUser.email());
        refreshToken.setExpiryDate(Instant.now().plusMillis(3600000)); // Expirera dans 1 heure
        return refreshToken;
    }

    public static MultiValueMap<String, String> buildRefreshAccessTokenParams() {
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("provider", TEST_PROVIDER);
        params.add("email", TEST_USER_EMAIL);
        return params;
    }

    public static MockResponse mockServerResponseNewAccessToken(String accessToken, String refreshToken, String scope) {
        StringBuilder jsonResponseBuilder = new StringBuilder();
        jsonResponseBuilder.append("{\n")
                .append("  \"access_token\": \"").append(accessToken).append("\",\n")
                .append("  \"token_type\": \"Bearer\",\n")
                .append("  \"expires_in\": 3600,\n")
                .append("  \"refresh_token\": \"").append(refreshToken).append("\",\n")
                .append("  \"scope\": \"").append(scope).append("\"\n")
                .append("}");

        String jsonResponse = jsonResponseBuilder.toString();

        return new MockResponse()
                .newBuilder()
                .body(jsonResponse)
                .addHeader("Content-Type", "application/json")
                .build();
    }

    public static MockResponse mockServerResponseUserDto(UserDto userDto) {
        StringBuilder jsonResponseBuilder = new StringBuilder();
        jsonResponseBuilder.append("{\n")
                .append("  \"name\": \"").append(userDto.name()).append("\",\n")
                .append("  \"email\": \"").append(userDto.email()).append("\",\n")
                .append("  \"imgUrl\": \"").append(userDto.imgUrl()).append("\",\n")
                .append("  \"country\": \"").append(userDto.country()).append("\",\n")
                .append("  \"provider\": \"").append(userDto.provider()).append("\",\n")
                .append("  \"id\": \"").append(userDto.userProviderId()).append("\",\n")
                .append("  \"roles\": [\"ROLE_USER\"]\n")
                .append("}");

        String jsonResponse = jsonResponseBuilder.toString();

        return new MockResponse()
                .newBuilder()
                .body(jsonResponse)
                .addHeader("Content-Type", "application/json")
                .build();
    }

    public static ClientRegistration createMockClientRegistration(String tokenUri) {
        ClientRegistration clientRegistration = mock(ClientRegistration.class);
        ClientRegistration.ProviderDetails providerDetails = mock(ClientRegistration.ProviderDetails.class);

        when(clientRegistration.getProviderDetails()).thenReturn(providerDetails);
        when(clientRegistration.getClientId()).thenReturn("client-id");
        when(clientRegistration.getClientSecret()).thenReturn("client-secret");
        when(clientRegistration.getRegistrationId()).thenReturn("spotify");
        when(providerDetails.getTokenUri()).thenReturn(tokenUri);

        return clientRegistration;
    }

    public static OAuth2AuthorizedClient createMockAuthorizedClient(ClientRegistration clientRegistration) {
        OAuth2AccessToken accessToken = new OAuth2AccessToken(OAuth2AccessToken.TokenType.BEARER, "access-token", Instant.now(), Instant.now().plusSeconds(3600));
        OAuth2RefreshToken refreshToken = new OAuth2RefreshToken("refresh-token", Instant.now());
        return new OAuth2AuthorizedClient(clientRegistration, "test-user@example.com", accessToken, refreshToken);
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
