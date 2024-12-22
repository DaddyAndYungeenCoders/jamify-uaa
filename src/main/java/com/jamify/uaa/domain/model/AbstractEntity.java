package com.jamify.uaa.domain.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.Objects;

/**
 * Abstract base class for entities. Provides common properties such as id, createdDate, and lastModifiedDate.
 *
 * @param <T> the type of the identifier
 */
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public abstract class AbstractEntity<T extends Serializable> implements Serializable {

    /**
     * The unique identifier for the entity.
     */
    @Setter
    @Getter
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", updatable = false, nullable = false)
    private T id;

    /**
     * The date and time when the entity was created.
     */
    @CreatedDate
    @Column(name = "created_date", nullable = false, updatable = false)
    private ZonedDateTime createdDate;

    /**
     * The date and time when the entity was last modified.
     */
    @LastModifiedDate
    @Column(name = "last_modified_date")
    private ZonedDateTime lastModifiedDate;

    /**
     * Checks if this entity is equal to another object.
     *
     * @param o the object to compare with
     * @return true if the objects are equal, false otherwise
     */
    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        AbstractEntity<?> that = (AbstractEntity<?>) o;
        return Objects.equals(id, that.id) && Objects.equals(createdDate, that.createdDate) && Objects.equals(lastModifiedDate, that.lastModifiedDate);
    }

    /**
     * Returns the hash code of this entity.
     *
     * @return the hash code
     */
    @Override
    public int hashCode() {
        return Objects.hash(id, createdDate, lastModifiedDate);
    }
}