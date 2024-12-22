package com.jamify.uaa.controller;

import com.jamify.uaa.domain.dto.UserDto;
import com.jamify.uaa.domain.mapper.UserMapper;
import com.jamify.uaa.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user")
public class UserController {

    private final UserService userService;
    private final UserMapper userMapper;

    private static final Logger log = LoggerFactory.getLogger(UserController.class);

    public UserController(UserService userService, UserMapper userMapper) {
        this.userService = userService;
        this.userMapper = userMapper;
    }

    @GetMapping
    public ResponseEntity<UserDto> getUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String principal = authentication.getPrincipal().toString();
        log.info("Logged in user: {}", principal);
        return ResponseEntity.ok(userMapper.toDto(userService.getUserByEmail(principal)));
    }

    //TODO: Endpoints

}
