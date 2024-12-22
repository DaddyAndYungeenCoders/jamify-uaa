package com.jamify.uaa.service;

import com.jamify.uaa.domain.model.UserEntity;

public interface UserService {
    void createUserIfNotExists(String email, String name, String country, String id, String imgUrl, String provider);

    UserEntity getUserByEmail(String email);
}
