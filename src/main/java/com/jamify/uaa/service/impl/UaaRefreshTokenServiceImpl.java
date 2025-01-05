package com.jamify.uaa.service.impl;

import com.jamify.uaa.domain.model.UaaRefreshToken;
import com.jamify.uaa.domain.model.UserEntity;
import com.jamify.uaa.repository.UaaRefreshTokenRepository;
import com.jamify.uaa.service.UaaRefreshTokenService;
import com.jamify.uaa.service.UserService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.UUID;

/**
 * Service implementation for managing UAA JWT refresh tokens.
 */
@Service
public class UaaRefreshTokenServiceImpl implements UaaRefreshTokenService {

    @Value("${security.jwt.refresh-token.expiration}")
    private Long refreshTokenExpirationMs;

    private final UaaRefreshTokenRepository refreshTokenRepository;
    private final UserService userService;

    /**
     * Constructor for UaaRefreshTokenServiceImpl.
     *
     * @param refreshTokenRepository the repository for managing refresh tokens
     * @param userService the service for managing users
     */
    public UaaRefreshTokenServiceImpl(UaaRefreshTokenRepository refreshTokenRepository, UserService userService) {
        this.refreshTokenRepository = refreshTokenRepository;
        this.userService = userService;
    }

    /**
     * Creates a new refresh token for the specified user.
     *
     * @param userId the ID of the user
     * @return the created refresh token
     */
    @Override
    public UaaRefreshToken createRefreshToken(Long userId) {
        // check if a refresh token already exists for the user
        UaaRefreshToken existingToken = refreshTokenRepository.findByUser(userService.getUserById(userId));
        if (existingToken != null) {
            if (isRefreshTokenExpired(existingToken)) {
                deleteUserRefreshToken(userId);
                return createRefreshToken(userId);
            }
            return existingToken;
        }
        UaaRefreshToken refreshToken = new UaaRefreshToken();
        refreshToken.setUser(userService.getUserById(userId));
        refreshToken.setToken(UUID.randomUUID().toString());
        refreshToken.setExpiryDate(Instant.now().plusMillis(refreshTokenExpirationMs));
        return refreshTokenRepository.save(refreshToken);
    }

    /**
     * Checks if the specified refresh token is expired.
     *
     * @param refreshToken the refresh token to check
     * @return true if the refresh token is expired, false otherwise
     */
    @Override
    public boolean isRefreshTokenExpired(UaaRefreshToken refreshToken) {
        if (refreshToken.getExpiryDate().compareTo(Instant.now()) < 0) {
            refreshTokenRepository.delete(refreshToken);
            return true;
        }
        return false;
    }

    /**
     * Retrieves the refresh token for the specified user.
     *
     * @param user the user entity
     * @return the refresh token for the user
     */
    @Override
    public UaaRefreshToken getTokenByUser(UserEntity user) {
        return refreshTokenRepository.findByUser(user);
    }

    /**
     * Deletes the refresh token for the specified user.
     *
     * @param userId the ID of the user
     */
    @Override
    public void deleteUserRefreshToken(Long userId) {
        UaaRefreshToken existingToken = refreshTokenRepository.findByUser(userService.getUserById(userId));
        if (existingToken != null) {
            refreshTokenRepository.delete(existingToken);
        }
    }
}
