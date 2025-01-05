package com.jamify.uaa.controller;

import com.jamify.uaa.domain.model.UserEntity;
import com.jamify.uaa.service.TokenService;
import com.jamify.uaa.service.UserService;
import com.jamify.uaa.utils.TestsUtils;
import mockwebserver3.MockResponse;
import mockwebserver3.MockResponseBody;
import mockwebserver3.MockWebServer;
import mockwebserver3.RecordedRequest;
import okhttp3.Headers;
import okhttp3.MediaType;
import okhttp3.ResponseBody;
import org.junit.jupiter.api.*;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.core.OAuth2RefreshToken;
import org.springframework.security.oauth2.core.endpoint.OAuth2AccessTokenResponse;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.time.Instant;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Sql(value = {"classpath:sql/insert_data_before_tests.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(value = {"classpath:sql/delete_data_and_init_seq.sql"}, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
class AuthControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TokenService tokenService;

    @Autowired
    private UserService userService;

    @MockitoBean
    private OAuth2AuthorizedClientService authorizedClientService;

    private static MockWebServer mockBackEnd;
    private WebClient webClient;

    @Value("${config.jamify-engine.api-key}")
    private String correctJamifyEngineApiKey;


    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);

        //TODO: do this just before tests that need to call external services
        String baseUrl = String.format("http://localhost:%s", mockBackEnd.getPort());
        webClient = WebClient.create(baseUrl);
    }

    @BeforeAll
    static void setUpWebServer() throws IOException {
        mockBackEnd = new MockWebServer();
        mockBackEnd.start();
    }

    @AfterAll
    static void tearDown() throws IOException {
        mockBackEnd.shutdown();
    }

    @Test
    void refreshJwtToken_withExpiredJwtAndValidRefreshToken_shouldReturnNewValidJwtToken() throws Exception {
        Long userId = 1L;
        String expiredJwt = TestsUtils.buildExpiredJwt(userService.getUserById(userId));

        mockMvc.perform(post("/api/v1/auth/refresh-jwt-token")
                        .header("Authorization", "Bearer " + expiredJwt))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").isNotEmpty());
    }

    @Test
    void refreshJwtToken_withExpiredRefreshToken_shouldReturnHttp401Response() throws Exception {
        Long userId = 2L; // user with id 2 has an expired refresh token
        UserEntity user = userService.getUserById(userId);
        String expiredJwt = TestsUtils.buildExpiredJwt(user);

        mockMvc.perform(post("/api/v1/auth/refresh-jwt-token")
                        .header("Authorization", "Bearer " + expiredJwt))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void refreshAccessToken_withInvalidApiKey_shouldReturnHttp403Response() throws Exception {
        MultiValueMap<String, String> params = TestsUtils.buildRefreshAccessTokenParams();
        String validJwt = TestsUtils.buildValidJwt(userService.getUserById(1L));

        mockMvc.perform(post("/api/v1/auth/refresh-access-token")
                        .header("X-API-KEY", "invalid-api-key")
                        .header("Authorization", "Bearer " + validJwt)
                        .params(params))
                .andExpect(status().isForbidden());
    }

    @Test
    void refreshAccessToken_withValidApiKey_shouldReturnNewAccessToken() throws Exception {
        MultiValueMap<String, String> params = TestsUtils.buildRefreshAccessTokenParams();
        String validJwt = TestsUtils.buildValidJwt(userService.getUserById(1L));
        Map<String, String> newTokens = Map.of("access-token", "new-access-token");

        // Mock the ClientRegistration
        ClientRegistration clientRegistration = mock(ClientRegistration.class);
        ClientRegistration.ProviderDetails providerDetails = mock(ClientRegistration.ProviderDetails.class);
        when(clientRegistration.getProviderDetails()).thenReturn(providerDetails);
        when(clientRegistration.getClientId()).thenReturn("client-id");
        when(clientRegistration.getClientSecret()).thenReturn("client-secret");
        when(clientRegistration.getRegistrationId()).thenReturn("spotify");
        when(providerDetails.getTokenUri()).thenReturn("http://localhost:" + mockBackEnd.getPort() + "/mocked-token-uri");

        // Mock the OAuth2AccessToken
        OAuth2AccessToken accessToken = new OAuth2AccessToken(OAuth2AccessToken.TokenType.BEARER, "access-token", Instant.now(), Instant.now().plusSeconds(3600));

        // Mock the OAuth2RefreshToken
        OAuth2RefreshToken refreshToken = new OAuth2RefreshToken("refresh-token", Instant.now());

        // Mock the OAuth2AuthorizedClient
        OAuth2AuthorizedClient authorizedClient = new OAuth2AuthorizedClient(clientRegistration, "test-user@example.com", accessToken, refreshToken);
        when(authorizedClientService.loadAuthorizedClient("spotify", "test-user@example.com")).thenReturn(authorizedClient);

        // Simuler la réponse du serveur pour le rafraîchissement du token
        StringBuilder jsonResponseBuilder = new StringBuilder();
        jsonResponseBuilder.append("{\n")
                .append("  \"access_token\": \"new-access-token\",\n")
                .append("  \"token_type\": \"Bearer\",\n")
                .append("  \"expires_in\": 3600,\n")
                .append("  \"refresh_token\": \"new-refresh-token\",\n")
                .append("  \"scope\": \"user-read-email user-read-private\"\n")
                .append("}");

        String jsonResponse = jsonResponseBuilder.toString();

        MockResponse mockResponse = new MockResponse()
                .newBuilder()
                .body(jsonResponse)
                .addHeader("Content-Type", "application/json")
                .build();

        // Enregistrer la réponse dans le serveur mocké
        mockBackEnd.enqueue(mockResponse);

        // perform the request
        mockMvc.perform(post("/api/v1/auth/refresh-access-token")
                        .header("X-API-KEY", correctJamifyEngineApiKey)
                        .header("Authorization", "Bearer " + validJwt)
                        .params(params))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.access_token").value("new-access-token")
                );

        // Vérifier la requête envoyée au serveur mocké
        RecordedRequest recordedRequest = mockBackEnd.takeRequest();
        Assertions.assertEquals("POST", recordedRequest.getMethod());
        Assertions.assertEquals("/mocked-token-uri", recordedRequest.getPath());
        Headers headers = recordedRequest.getHeaders();
        Assertions.assertEquals(validJwt, headers.get("Authorization"));
        Assertions.assertEquals(correctJamifyEngineApiKey, headers.get("X-API-KEY"));
    }
}
