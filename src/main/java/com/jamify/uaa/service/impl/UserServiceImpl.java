package com.jamify.uaa.service.impl;


import com.jamify.uaa.domain.model.UserEntity;
import com.jamify.uaa.repository.UserRepository;
import com.jamify.uaa.service.UserService;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public void createUserIfNotExists(String email, String name, String country, String id, String imgUrl, String provider) {
        if (!userRepository.existsByEmail(email)) {
            UserEntity user = new UserEntity();
            user.setEmail(email);
            user.setName(name);
            user.setRole("ROLE_USER");
            user.setCountry(country);
            user.setProviderId(id);
            user.setImgUrl(imgUrl);
            user.setProvider(provider);

            userRepository.save(user);
        }
    }
}
