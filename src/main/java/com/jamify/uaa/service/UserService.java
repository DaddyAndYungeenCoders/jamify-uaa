package com.jamify.uaa.service;

import com.jamify.uaa.domain.model.UserEntity;

/**
 * Service interface for managing user-related operations.
 */
public interface UserService {

    /**
     * Creates a new user if one does not already exist with the given email.
     *
     * @param email    the email of the user
     * @param name     the name of the user
     * @param country  the country of the user
     * @param id       the unique identifier of the user from the provider
     * @param imgUrl   the image URL of the user
     * @param provider the provider of the user
     */
    void createUserIfNotExists(String email, String name, String country, String id, String imgUrl, String provider);

    /**
     * Retrieves a user by their email.
     *
     * @param email the email of the user
     * @return the UserEntity associated with the given email
     */
    UserEntity getUserByEmail(String email);
}