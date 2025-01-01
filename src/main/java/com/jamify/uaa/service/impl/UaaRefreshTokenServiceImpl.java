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

@Service
public class UaaRefreshTokenServiceImpl implements UaaRefreshTokenService {

    @Value("${security.jwt.refresh-token.expiration}")
    private Long refreshTokenExpirationMs;

    private final UaaRefreshTokenRepository refreshTokenRepository;
    private final UserService userService;

    public UaaRefreshTokenServiceImpl(UaaRefreshTokenRepository refreshTokenRepository, UserService userService) {
        this.refreshTokenRepository = refreshTokenRepository;
        this.userService = userService;
    }

    @Override
    public UaaRefreshToken createRefreshToken(Long userId) {
        UaaRefreshToken refreshToken = new UaaRefreshToken();
        refreshToken.setUser(userService.getUserById(userId));
        refreshToken.setToken(UUID.randomUUID().toString());
        refreshToken.setExpiryDate(Instant.now().plusMillis(refreshTokenExpirationMs));
        return refreshTokenRepository.save(refreshToken);
    }

    @Override
    public boolean isRefreshTokenExpired(UaaRefreshToken refreshToken) {
        if (refreshToken.getExpiryDate().compareTo(Instant.now()) < 0) {
            refreshTokenRepository.delete(refreshToken);
            return true;
        }
        return false;

    }

    @Override
    public UaaRefreshToken getTokenByUser(UserEntity user) {
        return refreshTokenRepository.findByUser(user);
    }

}
