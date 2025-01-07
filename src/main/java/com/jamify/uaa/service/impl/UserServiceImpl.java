package com.jamify.uaa.service.impl;


import com.jamify.uaa.config.service.CustomOAuth2User;
import com.jamify.uaa.constants.Role;
import com.jamify.uaa.domain.dto.UserDto;
import com.jamify.uaa.service.UserService;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Collections;

@Service
public class UserServiceImpl implements UserService {

    private final WebClient jamifyEngineWebClient;

    public UserServiceImpl(WebClient jamifyEngineWebClient) {
        this.jamifyEngineWebClient = jamifyEngineWebClient;
    }

    @Override
    public UserDto getUserByEmail(String email) {
        // api call to jamify-engine
        return jamifyEngineWebClient.get()
                .uri("/users/uaa/email/{email}", email)
                .retrieve()
                .bodyToMono(UserDto.class)
                .block();
    }

    @Override
    public UserDto getUserById(Long userId) {
        // api call to jamify-engine
        return jamifyEngineWebClient.get()
                .uri("/users/{userId}", userId)
                .retrieve()
                .bodyToMono(UserDto.class)
                .block();
    }

    @Override
    public void sendLoggedUserToEngineForCreation(CustomOAuth2User oauthUser, String provider) {
        //TODO: check provider id beacause its null
        UserDto userDto = new UserDto(
                oauthUser.getName(),
                oauthUser.getEmail(),
                oauthUser.getImgUrl(),
                oauthUser.getCountry(),
                provider,
                oauthUser.getId(), // id from provider
                Collections.singletonList(Role.USER.getValue()) // TODO: change if role are implemented
        );
        // create user with jamify-engine
        jamifyEngineWebClient.post()
                .uri("/users/uaa/create")
                .bodyValue(userDto)
                .retrieve()
                .bodyToMono(Void.class)
                .block();
    }
}
