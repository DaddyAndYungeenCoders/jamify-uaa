package com.jamify.uaa.controller;

import com.jamify.uaa.service.TokenService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    @Value("${security.api-key.jamify-engine}")
    private String jamifyEngineApiKey;

    private final TokenService tokenService;

    public AuthController(TokenService tokenService) {
        this.tokenService = tokenService;
    }

    // Jamify engine will call this endpoint if the access token is expired
    // it needs the X-API-KEY header to be set to the value of the API key
    @PostMapping("/refresh-token")
    public ResponseEntity<Map<String, String>> refreshAccessToken(
            @RequestHeader(value = "X-API-KEY") String apiKey,
            @RequestParam String provider,
            @RequestParam String email
    ) {
        if (!apiKey.equals(jamifyEngineApiKey)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("error", "Invalid API key"));
        }

        Map<String, String> tokenResponse = tokenService.refreshAccessToken(provider, email);
        if (tokenResponse.containsKey("error")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(tokenResponse);
        }
        return ResponseEntity.ok(tokenResponse);
    }

}

// côté jamify engine

//@Service
//public class TokenService {
//
//    private final UserTokenRepository userTokenRepository;
//    private final RestTemplate restTemplate;
//
//    public TokenService(UserTokenRepository userTokenRepository, RestTemplate restTemplate) {
//        this.userTokenRepository = userTokenRepository;
//        this.restTemplate = restTemplate;
//    }
//
//    public String getAccessToken(String email, String provider) {
//        UserToken token = userTokenRepository.findByEmailAndProvider(email, provider)
//                .orElseThrow(() -> new RuntimeException("Token not found"));
//
//        if (token.getExpiresAt().isBefore(LocalDateTime.now())) {
//            log.info("Access token expired. Refreshing...");
//            String refreshedToken = refreshAccessToken(email, provider);
//            token.setAccessToken(refreshedToken);
//            token.setExpiresAt(LocalDateTime.now().plusHours(1));
//            userTokenRepository.save(token);
//        }
//
//        return token.getAccessToken();
//    }
//
//    private String refreshAccessToken(String email, String provider) {
//      @Value("${security.api-key.jamify-engine}")
//      private String jamifyEngineApiKey;
//
//      HttpHeaders headers = new HttpHeaders();
//      headers.set("X-API-KEY", "your-api-key");
//
//      HttpEntity<Void> requestEntity = new HttpEntity<>(headers);
//
//      ResponseEntity<Map> response = restTemplate.exchange(
//        "http://uaa-service/api/v1/auth/refresh-token?provider=" + provider + "&email=" + email,
//        HttpMethod.POST,
//        requestEntity,
//        Map.class
//);
//
//        if (response.getStatusCode().is2xxSuccessful()) {
//            return (String) response.getBody().get("access_token");
//        } else {
//            throw new RuntimeException("Failed to refresh access token");
//        }
//    }
//}
