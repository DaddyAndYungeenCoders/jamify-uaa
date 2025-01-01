package com.jamify.uaa.controller;

import com.jamify.uaa.config.service.JwtService;
import com.jamify.uaa.domain.model.UserEntity;
import com.jamify.uaa.service.TokenService;
import com.jamify.uaa.service.UserService;
import com.jamify.uaa.utils.TestsUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.util.MultiValueMap;

import java.util.Map;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Sql(value = {"classpath:sql/insert_data_before_tests.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(value = {"classpath:sql/delete_data_and_init_seq.sql"}, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
public class AuthControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TokenService tokenService;

    @Autowired
    private UserService userService;

    @Value("${security.api-key.jamify-engine}")
    private String correctJamifyEngineApiKey;

    @Test
    void refreshJwtToken_withExpiredToken_shouldReturnNewValidJwtToken() throws Exception {
        Long userId = 1L;
        String expiredJwt = TestsUtils.buildExpiredJwt(userService.getUserById(userId));

        mockMvc.perform(post("/api/v1/auth/refresh-jwt-token")
                        .header("Authorization", "Bearer " + expiredJwt))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").isNotEmpty());
    }

//    @Test
//    void refreshJwtToken_withInvalidRefreshToken_shouldReturnHttp401Response() throws Exception {
//        Long userId = 1L;
//        UserEntity user = userService.getUserById(userId);
//        user.setEmail("wrong-mail@fake.com");
//        String expiredJwt = TestsUtils.buildExpiredJwt(user);
//
//        mockMvc.perform(post("/api/v1/auth/refresh-jwt-token")
//                        .header("Authorization", "Bearer " + expiredJwt))
//                .andExpect(status().isUnauthorized());
//    }

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

//    @Test
//    void refreshAccessToken_withValidApiKey_shouldReturnNewAccessToken() throws Exception {
//        MultiValueMap<String, String> params = TestsUtils.buildRefreshAccessTokenParams();
//        String validJwt = TestsUtils.buildValidJwt(userService.getUserById(1L));
//
//        // need to mock jwtService.refreshAccessToken because it is a call to spotify api
//        // when we call this method, it will return a new access token
//        Map<String, String> newTokens = Map.of("access-token", "new-access-token");
//        when(tokenService.refreshAccessToken(anyString(), anyString())).thenReturn(newTokens);
//
//        mockMvc.perform(post("/api/v1/auth/refresh-access-token")
//                        .header("X-API-KEY", correctJamifyEngineApiKey)
//                        .header("Authorization", "Bearer " + validJwt)
//                        .params(params))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.accessToken").isNotEmpty());
//    }
}
