package com.example.bankcards.service;

import com.example.bankcards.config.property.AppProperties;
import com.example.bankcards.entity.User;
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

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtService {
    private final UserService userService;
    private final AppProperties.JwtProperties properties;

    public String generateTokenFromUsername(String username) {
        var user = userService.loadUserByUsername(username);
        return Jwts.builder()
                .header().type("JWT").and()
                .subject(user.getId().toString())
                .claims(new HashMap<>() {{
                    put("email", user.getUsername());
                    put("roles", user.getAuthorities());
                }})
                .issuedAt(new Date())
                .expiration(new Date(new Date().getTime() + properties.tokenExpiration().toMillis()))
                .signWith(Keys.hmacShaKeyFor(properties.secret().getBytes(StandardCharsets.UTF_8)))
                .compact();
    }

    public User getUserDetails(String token) {
        try {
            String email = Jwts.parser()
                    .verifyWith(Keys.hmacShaKeyFor(properties.secret().getBytes(StandardCharsets.UTF_8)))
                    .build().parseSignedClaims(token).getPayload().get("email").toString();
            return userService.findByEmail(email);
        } catch(ExpiredJwtException e) {
            return userService.findByEmail(e.getClaims().get("email").toString());
        }
    }

    @Cacheable(value = "isValid", key = "{ #token }", sync = true)
    public boolean validate(String token) {
        try {
            var  claim  = Jwts.parser()
                    .verifyWith(Keys.hmacShaKeyFor(properties.secret().getBytes(StandardCharsets.UTF_8)))
                    .build().parseSignedClaims(token).getPayload();
            return userService.findByEmail(claim.get("email").toString()).isEnabled();
        } catch (JwtException | IllegalArgumentException e) {
            log.error("Claims string is empty: {}", e.getMessage());
            return false;
        }
    }
}
