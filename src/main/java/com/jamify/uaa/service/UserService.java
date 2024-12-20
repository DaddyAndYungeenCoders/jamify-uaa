package com.jamify.uaa.service;

public interface UserService {
    void createUserIfNotExists(String email, String name, String country, String id, String imgUrl, String provider);
}
