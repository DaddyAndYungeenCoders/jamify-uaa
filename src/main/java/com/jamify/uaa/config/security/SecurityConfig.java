package com.jamify.uaa.config.security;

import com.jamify.uaa.config.LoggingFilter;
import com.jamify.uaa.config.service.CustomAuthenticationSuccessHandler;
import com.jamify.uaa.config.service.CustomOAuth2UserService;
import com.jamify.uaa.config.service.JwtService;
import com.jamify.uaa.constants.Role;
import com.jamify.uaa.service.UaaRefreshTokenService;
import com.jamify.uaa.service.UserService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

/**
 * Security configuration class for the application.
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Value("${gateway.url}")
    private String gatewayUrl;

    /**
     * Configures the security filter chain.
     *
     * @param http                    the HttpSecurity object to configure
     * @param customOAuth2UserService the custom OAuth2 user service
     * @param jwtAuthenticationFilter the JWT authentication filter
     * @param userService             the user service
     * @return the configured SecurityFilterChain
     * @throws Exception if an error occurs during configuration
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http, CustomOAuth2UserService customOAuth2UserService, JwtAuthenticationFilter jwtAuthenticationFilter, UserService userService, OAuth2AuthorizedClientService authorizedClientService, UaaRefreshTokenService uaaRefreshTokenService) throws Exception {
        http
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .csrf(AbstractHttpConfigurer::disable)
                .addFilterBefore(new LoggingFilter(), UsernamePasswordAuthenticationFilter.class)  // Add LoggingFilter before the authentication filter
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .sessionManagement(sessionManagement -> sessionManagement
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers("/", "/auth/**", "/login/**", "/oauth2/**", "/oauth/.well-known/jwks.json").permitAll()
                        .requestMatchers("/swagger-ui.html", "/v3/api-docs/**", "/swagger-ui/**", "/v3/api-docs").permitAll()
                        .requestMatchers("/api/v1/auth/refresh-jwt-token").permitAll()
                        .requestMatchers("/api/v1/user").hasAuthority(Role.USER.getValue())
                        .anyRequest().authenticated()
                )
                .oauth2Login(oauth2 -> oauth2
                        .userInfoEndpoint(userInfo -> userInfo
                                .userService(customOAuth2UserService)
                        )
                        .successHandler(oAuth2AuthenticationSuccessHandler(userService, authorizedClientService, uaaRefreshTokenService))
                )
                .logout(logout -> logout
                        .logoutUrl("/auth/logout")
                        .clearAuthentication(true)
                        .deleteCookies("jamify-auth")
                        .logoutSuccessHandler((request, response, authentication) -> response.sendRedirect("/"))
                )
                .exceptionHandling(exceptionHandling -> exceptionHandling
                        .authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED)));

        return http.build();
    }

    /**
     * Creates a bean for the OAuth2 authentication success handler.
     *
     * @param userService the user service
     * @return the authentication success handler
     */
    @Bean
    public AuthenticationSuccessHandler oAuth2AuthenticationSuccessHandler(UserService userService, OAuth2AuthorizedClientService authorizedClientService, UaaRefreshTokenService uaaRefreshTokenService) {
        return new CustomAuthenticationSuccessHandler(jwtService(), userService, uaaRefreshTokenService, authorizedClientService);
    }

    /**
     * Creates a bean for the JWT service.
     *
     * @return the JWT service
     */
    @Bean
    public JwtService jwtService() {
        return new JwtService();
    }

    /**
     * Creates a bean for the CORS configuration source.
     *
     * @return the CORS configuration source
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of(gatewayUrl,
                "http://10.56.97.242:8083",
                "http://localhost:8083",
                "http://10.56.97.242:8081",
                "http://localhost:5173",
                "http:/192.168.104.121:5173")
        );
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("Authorization", "Cache-Control", "Content-Type"));
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        source.registerCorsConfiguration("/v3/api-docs/**", configuration);
        source.registerCorsConfiguration("/swagger-ui/**", configuration);
        source.registerCorsConfiguration("/swagger-ui.html", configuration);
        return source;
    }
}