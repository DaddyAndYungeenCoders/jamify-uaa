package com.jamify.uaa.repository;

import com.jamify.uaa.domain.model.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repository interface for accessing UserEntity data from the database.
 */
public interface UserRepository extends JpaRepository<UserEntity, Long> {

    /**
     * Checks if a user exists by their email.
     *
     * @param email the email to check
     * @return true if a user with the given email exists, false otherwise
     */
    boolean existsByEmail(String email);

    /**
     * Finds a user by their email.
     *
     * @param email the email to search for
     * @return the UserEntity with the given email, or null if no user is found
     */
    UserEntity findByEmail(String email);
}