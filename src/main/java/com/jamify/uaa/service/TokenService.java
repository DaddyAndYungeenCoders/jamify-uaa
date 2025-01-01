package com.jamify.uaa.service;

import java.util.Map;

/**
 * Service interface for managing access tokens from providers.
 */
public interface TokenService {

    /**
     * Refreshes the access token for the specified provider and email.
     *
     * @param provider the provider for which the access token is to be refreshed
     * @param email the email of the user for whom the access token is to be refreshed
     * @return a map containing the new access token
     */
    Map<String, String> refreshAccessToken(String provider, String email);
}