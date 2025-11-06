package com.example.bankcards.service;

import com.example.bankcards.config.property.AppProperties;
import com.example.bankcards.entity.User;
import com.example.bankcards.security.UserService;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;

import io.jsonwebtoken.Jwts;

import javax.management.timer.Timer;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtService {
    private final UserService userService;
    private final AppProperties.JwtProperties properties;

    /**
     * Forms JWT token character sequence.
     * Main JWT token generation method.
     * @param username   user name for JWT token
     *
     * @return  JWT token
     */
    public String create(String username) {
        var user = userService.loadUserByUsername(username);
        var moment = new Date();
        return Jwts.builder()
                .header().type("JWT").and()
                .subject(user.getId().toString())
                .claims(new HashMap<>() {{
                    put("email", user.getUsername());
                    put("roles", user.getAuthorities());
                }})
                .issuedAt(moment)
                .expiration(new Date(moment.getTime() + properties.tokenExpiration().toMillis() * Timer.ONE_MINUTE))
                .signWith(Keys.hmacShaKeyFor(properties.secret().getBytes(StandardCharsets.UTF_8)))
                .compact();
    }

    /**
     * Parses user credentials according to JWT token.
     * Main user record and JWT token matching method.
     * @param token   JWT token
     *
     * @return  Database user record representation object
     */
    public User find(String token) {
        try {
            String email = Jwts.parser()
                    .verifyWith(Keys.hmacShaKeyFor(properties.secret().getBytes(StandardCharsets.UTF_8)))
                    .build().parseSignedClaims(token).getPayload().get("email").toString();
            return userService.find(email);
        } catch(ExpiredJwtException e) {
            return userService.find(e.getClaims().get("email").toString());
        }
    }

    /**
     * Validate user JWT token.
     * Main JWT token validation method.
     * @param token   JWT token
     *
     * @return  JWT token validity indicator
     */
    @Cacheable(value = "isValid", key = "{ #token }", sync = true)
    public boolean validate(String token) {
        try {
            var  email  = Jwts.parser()
                    .verifyWith(Keys.hmacShaKeyFor(properties.secret().getBytes(StandardCharsets.UTF_8)))
                    .build().parseSignedClaims(token).getPayload().get("email").toString();
            return userService.find(email).isEnabled();
        } catch (JwtException | IllegalArgumentException e) {
            log.error("Claims string is empty: {}", e.getMessage());
            return false;
        }
    }
}
