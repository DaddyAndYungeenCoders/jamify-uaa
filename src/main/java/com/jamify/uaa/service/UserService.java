package com.jamify.uaa.service;

import com.jamify.uaa.config.service.CustomOAuth2User;
import com.jamify.uaa.domain.dto.UserDto;

/**
 * Service interface for managing user-related operations.
 */
public interface UserService {

    /**
     * Sends the logged-in user to the engine for creation.
     *
     * @param oauthUser the OAuth2 user information
     * @param provider the OAuth2 provider
     */
    void sendLoggedUserToEngineForCreation(CustomOAuth2User oauthUser, String provider);

    /**
     * Retrieves a user by their email.
     *
     * @param email the email of the user
     * @return the UserDto associated with the given email
     */
    UserDto getUserByEmail(String email);

    /**
     * Retrieves a user by their unique identifier.
     *
     * @param userId the unique identifier of the user from the database
     * @return the UserDto associated with the given unique identifier
     */
    UserDto getUserById(Long userId);
}