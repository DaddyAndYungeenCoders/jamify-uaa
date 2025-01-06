package com.jamify.uaa.controller;

import com.jamify.uaa.domain.dto.UserDto;
import com.jamify.uaa.utils.TestsUtils;
import mockwebserver3.MockResponse;
import mockwebserver3.MockWebServer;
import mockwebserver3.RecordedRequest;
import org.junit.jupiter.api.*;
import org.mockito.Mock;
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
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.io.IOException;

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

    @MockitoBean
    private OAuth2AuthorizedClientService authorizedClientService;

    private static MockWebServer mockBackEnd;

    @MockitoBean
    private WebClient jamifyEngineWebClient;

    @Mock
    private WebClient.RequestHeadersUriSpec requestHeadersUriSpec;

    @Mock
    private WebClient.RequestHeadersSpec requestHeadersSpec;

    @Mock
    private WebClient.ResponseSpec responseSpec;

    @Value("${config.jamify-engine.api-key}")
    private String correctJamifyEngineApiKey;

    @BeforeAll
    static void setUpMockWebServer() throws IOException {
        mockBackEnd = new MockWebServer();
        mockBackEnd.start();
    }

    @AfterAll
    static void tearDownMockWebServer() throws IOException {
        if (mockBackEnd != null) {
            mockBackEnd.shutdown();
        }
    }

    @BeforeEach
    void setUp() {
        when(jamifyEngineWebClient.get()).thenReturn(requestHeadersUriSpec);
    }

    @Test
    void refreshJwtToken_withExpiredJwtAndValidRefreshToken_shouldReturnNewValidJwtToken() throws Exception {
        UserDto userDto = TestsUtils.buildUserDto();
        String expiredJwt = TestsUtils.buildExpiredJwt(userDto);

        // Configuration du mock WebClient
        when(requestHeadersUriSpec.uri("/users/uaa/email/{email}", userDto.email())).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(UserDto.class)).thenReturn(Mono.just(userDto));

        mockMvc.perform(post("/api/v1/auth/refresh-jwt-token")
                        .header("Authorization", "Bearer " + expiredJwt))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").isNotEmpty());
    }

    @Test
    void refreshJwtToken_withExpiredRefreshToken_shouldReturnHttp401Response() throws Exception {
        UserDto userDto = TestsUtils.buildUserDtoWithExpiredRefreshToken();
        String expiredJwt = TestsUtils.buildExpiredJwt(userDto);

        // Configuration du mock WebClient
        when(requestHeadersUriSpec.uri("/users/uaa/email/{email}", userDto.email())).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(UserDto.class)).thenReturn(Mono.just(userDto));

        mockMvc.perform(post("/api/v1/auth/refresh-jwt-token")
                        .header("Authorization", "Bearer " + expiredJwt))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void refreshAccessToken_withInvalidApiKey_shouldReturnHttp403Response() throws Exception {
        MultiValueMap<String, String> params = TestsUtils.buildRefreshAccessTokenParams();
        String validJwt = TestsUtils.buildValidJwt(TestsUtils.buildUserDto());

        mockMvc.perform(post("/api/v1/auth/refresh-access-token")
                        .header("X-API-KEY", "invalid-api-key")
                        .header("Authorization", "Bearer " + validJwt)
                        .params(params))
                .andExpect(status().isForbidden());
    }

    @Test
    void refreshAccessToken_withValidApiKey_shouldReturnNewAccessToken() throws Exception {
        MultiValueMap<String, String> params = TestsUtils.buildRefreshAccessTokenParams();
        UserDto userDto = TestsUtils.buildUserDto();
        String validJwt = TestsUtils.buildValidJwt(userDto);

        String newAccessToken = "new-access-token";
        String newRefreshToken = "new-refresh-token";
        String scope = "user-read-email user-read-private";

//         Mocking the response from the mock server
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
