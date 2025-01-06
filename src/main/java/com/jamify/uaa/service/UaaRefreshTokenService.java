package com.jamify.uaa.service;

import com.jamify.uaa.domain.model.UaaRefreshToken;
import com.jamify.uaa.domain.model.UserEntity;

/**
 * Service interface for managing UAA refresh tokens.
 */
public interface UaaRefreshTokenService {

    /**
     * Creates a new refresh token for the specified user ID.
     *
     * @param userId the ID of the user for whom the refresh token is to be created
     * @return the created UaaRefreshToken
     */
    UaaRefreshToken createRefreshToken(Long userId);

    /**
     * Checks if the given refresh token is expired.
     *
     * @param refreshToken the refresh token to check
     * @return true if the refresh token is expired, false otherwise
     */
    boolean isRefreshTokenExpired(UaaRefreshToken refreshToken);

    /**
     * Retrieves the refresh token associated with the specified user.
     *
     * @param userEntity the user entity for whom the refresh token is to be retrieved
     * @return the UaaRefreshToken associated with the user
     */
    UaaRefreshToken getTokenByUser(UserEntity userEntity);

    /**
     * Deletes the refresh token associated with the specified user.
     *
     * @param userId the ID of the user for whom the refresh token is to be deleted
     */
    void deleteUserRefreshToken(Long userId);
}