package com.jamify.uaa.repository;

import com.jamify.uaa.domain.model.OAuth2AuthorizedClientEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface OAuth2AuthorizedClientRepository extends JpaRepository<OAuth2AuthorizedClientEntity, Long> {
    Optional<OAuth2AuthorizedClientEntity> findFirstByClientRegistrationIdAndPrincipalNameOrderByIdDesc(
            String clientRegistrationId,
            String principalName
    );

    void deleteByClientRegistrationIdAndPrincipalName(
            String clientRegistrationId,
            String principalName
    );
}