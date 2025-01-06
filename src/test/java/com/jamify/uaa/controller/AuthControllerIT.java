package com.jamify.uaa.controller;

import com.jamify.uaa.domain.model.UserEntity;
import com.jamify.uaa.service.TokenService;
import com.jamify.uaa.service.UserService;
import com.jamify.uaa.utils.TestsUtils;
import mockwebserver3.MockResponse;
import mockwebserver3.MockWebServer;
import mockwebserver3.RecordedRequest;
import okhttp3.Headers;
import org.junit.jupiter.api.*;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.client.WebClient;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
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
        Long userId = 1L;

        MultiValueMap<String, String> params = TestsUtils.buildRefreshAccessTokenParams();
        UserEntity user = userService.getUserById(userId);
        String validJwt = TestsUtils.buildValidJwt(user);

        String newAccessToken = "new-access-token";
        String newRefreshToken = "new-refresh-token";
        String scope = "user-read-email user-read-private";

        // Mocking the response from the mock server
        MockResponse mockResponse = TestsUtils.mockServerResponseNewAccessToken(newAccessToken, newRefreshToken, scope);
        mockBackEnd.enqueue(mockResponse);

        // Mocking ClientRegistration and OAuth2AuthorizedClient
        String tokenUri = "http://localhost:" + mockBackEnd.getPort() + "/mocked-token-uri";
        ClientRegistration clientRegistration = TestsUtils.createMockClientRegistration(tokenUri);

        OAuth2AuthorizedClient authorizedClient = TestsUtils.createMockAuthorizedClient(clientRegistration);
        when(authorizedClientService.loadAuthorizedClient("spotify", "test-user@example.com")).thenReturn(authorizedClient);

        // perform the request and verify the response
        mockMvc.perform(post("/api/v1/auth/refresh-access-token")
                        .header("X-API-KEY", correctJamifyEngineApiKey)
                        .header("Authorization", "Bearer " + validJwt)
                        .params(params))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.access_token").value(newAccessToken));

        // Verify the request sent to the mock server
        RecordedRequest recordedRequest = mockBackEnd.takeRequest();
        Assertions.assertEquals("POST", recordedRequest.getMethod());
        Assertions.assertEquals("/mocked-token-uri", recordedRequest.getPath());
    }
}
