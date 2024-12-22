package com.jamify.uaa.config.security;

import org.springframework.data.domain.AuditorAware;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * SecurityAuditorAware is a Spring component that provides the current auditor's information
 * for auditing purposes in a Spring Data JPA application.
 * It implements the AuditorAware interface to return the current auditor's username.
 */
@Component
public class SecurityAuditorAware implements AuditorAware<String> {

    /**
     * Returns the current auditor's username.
     * If the user is not authenticated, it returns "System" as the default auditor.
     *
     * @return an Optional containing the auditor's username or "System" if not authenticated.
     */
    @Override
    public Optional<String> getCurrentAuditor() {
        if (SecurityContextHolder.getContext().getAuthentication() == null) {
            return Optional.of("System");
        }
        return Optional.of(SecurityContextHolder.getContext().getAuthentication().getName());
    }
}
