package com.jamify.uaa.domain.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class UserEntity extends AbstractEntity<Long> {

    private @NotNull String name;
    private @NotNull String email;
    private String role;

    private String country;
    private String provider;
    private String providerId;

    @Column(length = 1024) // for long imgurl (eg. spotify)
    private String imgUrl;

    @Override
    public String toString() {
        return "UserEntity{" +
                "name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", role='" + role + '\'' +
                ", country='" + country + '\'' +
                ", provider='" + provider + '\'' +
                ", providerId='" + providerId + '\'' +
                ", imgUrl='" + imgUrl + '\'' +
                '}';
    }
}
