package com.jamify.uaa.repository;

import com.jamify.uaa.domain.model.UaaRefreshToken;
import com.jamify.uaa.domain.model.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UaaRefreshTokenRepository extends JpaRepository<UaaRefreshToken, Long> {
    UaaRefreshToken findByUser(UserEntity user);
}
