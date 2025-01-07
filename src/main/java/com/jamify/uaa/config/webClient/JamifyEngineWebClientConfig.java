package com.jamify.uaa.config.webClient;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;

/**
 * Configuration class for UAA WebClient.
 */
@Configuration
public class JamifyEngineWebClientConfig {

    /**
     * API key for Jamify Engine.
     */
    @Value("${config.jamify-engine.api-key}")
    private String jamifyApiKey;

    /**
     * Base URL for UAA.
     */
    @Value("${config.jamify-engine.url.base}")
    private String jamifyEngineUrl;

    /**
     * Creates a WebClient bean configured for UAA.
     *
     * @return a configured WebClient instance
     */
    @Bean
    public WebClient jamifyEngineWebClient() {
        return WebClient.builder()
                .baseUrl(jamifyEngineUrl)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .defaultHeader("X-API-KEY", jamifyApiKey)
                .build();
    }
}