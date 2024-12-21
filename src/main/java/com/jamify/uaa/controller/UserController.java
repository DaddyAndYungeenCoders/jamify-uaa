package com.jamify.uaa.controller;

import com.jamify.uaa.domain.dto.UserDto;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/user")
public class UserController {

    private static final Logger log = LoggerFactory.getLogger(UserController.class);

    @Value("${jwt.secret}")
    private String jwtSecret;

//    @GetMapping
//    public ResponseEntity<UserDto> getUser(@AuthenticationPrincipal OAuth2User principal) {
//        System.out.println("Principal: " + principal); // Debug log
//        if (principal == null) {
//            System.out.println("Principal is null!"); // Debug log
//            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
//        }
//
//        UserDto userDTO = new UserDto();
//        userDTO.setEmail(principal.getAttribute("email"));
//        userDTO.setName(principal.getAttribute("display_name"));
//        return ResponseEntity.ok(userDTO);
//    }

    @GetMapping
    public ResponseEntity<?> getUser(@RequestHeader("Authorization") String token) {
        log.info("Token: {}", token);
//        try {
//            // Enlever "Bearer " du token
//            token = token.substring(7);
//            Claims claims = Jwts.parser()
//                    .setSigningKey(jwtSecret)
//                    .parseClaimsJws(token)
//                    .getBody();
//
//            Map<Object, Object> userInfo = new HashMap<>();
//            userInfo.put("id", claims.get("id"));
//            userInfo.put("email", claims.get("email"));
//            userInfo.put("name", claims.getSubject());
//
//            return ResponseEntity.ok(userInfo);
//        } catch (Exception e) {
//            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Token invalide");
//        }
        UserDto userDTO = new UserDto();
        userDTO.setEmail("gmail");
        userDTO.setName("name");
        return ResponseEntity.ok(userDTO);
    }
}
