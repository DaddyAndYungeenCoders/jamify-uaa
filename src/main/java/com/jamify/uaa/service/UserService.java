package com.jamify.uaa.service;

import com.jamify.uaa.domain.model.UserEntity;

/**
 * Service interface for managing user-related operations.
 */
public interface UserService {

    /**
     * Creates a new user if one does not already exist with the given email.
     *
     * @param email          the email of the user
     * @param name           the name of the user
     * @param country        the country of the user
     * @param idFromProvider the unique identifier of the user from the provider
     * @param imgUrl         the image URL of the user
     * @param provider       the provider of the user
     */
    void createUserIfNotExists(String email, String name, String country, String idFromProvider, String imgUrl, String provider);

    /**
     * Retrieves a user by their email.
     *
     * @param email the email of the user
     * @return the UserEntity associated with the given email
     */
    UserEntity getUserByEmail(String email);

    /**
     * Retrieves a user by their unique identifier.
     *
     * @param userId the unique identifier of the user from the database
     * @return the UserEntity associated with the given unique identifier
     */
    UserEntity getUserById(Long userId);
}