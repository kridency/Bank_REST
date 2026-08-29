package com.example.bankcards.security;

import com.example.bankcards.config.property.AppProperties;
import com.example.bankcards.entity.User;
import com.example.bankcards.repository.UserRepository;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

import java.util.*;

import javax.crypto.SecretKey;
import javax.management.timer.Timer;

@Slf4j
@Component
public class JwtService {
    private final UserRepository userRepository;
    private final AppProperties.JwtProperties properties;
    private final SecretKey secretKey;

    public JwtService(@Qualifier("jwtSigningKey") SecretKey secretKey,
                      UserRepository userRepository,
                      AppProperties.JwtProperties properties) {
        this.userRepository = userRepository;
        this.properties = properties;
        this.secretKey = secretKey;
    }

    /**
     * Forms JWT token character sequence.
     * Main JWT token generation method.
     * @param refreshTokenId   refresh token id for JWT token
     *
     * @return  JWT token
     */
    public String create(UUID refreshTokenId) {
        var user = userRepository.findById(refreshTokenId);
        var moment = new Date();
        return Jwts.builder()
                .header().type("JWT").and()
                .id(user.map(User::getId).map(UUID::toString).orElse(""))
                .subject(user.map(User::getUsername).orElse(""))
                .claims(new HashMap<>() {{
                    put("roles", user.map(User::getAuthorities).orElse(List.of()));
                }})
                .issuedAt(moment)
                .expiration(new Date(moment.getTime() + properties.tokenExpiration().toMillis() * Timer.ONE_MINUTE))
                .signWith(Keys.hmacShaKeyFor(secretKey.getEncoded()))
                .compact();
    }

    /**
     * Parses user credentials according to JWT token.
     * Main user record and JWT token matching method.
     * @param token   JWT token
     *
     * @return  Database user record representation object
     */
    public String getSubject(String token) {
        return Jwts.parser()
                .verifyWith(Keys.hmacShaKeyFor(secretKey.getEncoded()))
                .build().parseSignedClaims(token).getPayload().getSubject();

    }

    /**
     * Validate user JWT token.
     * Main JWT token validation method.
     * @param token   JWT token
     *
     * @return  JWT token validity indicator
     */
    @Cacheable(value = "isValid", key = "{ #token }", sync = true)
    public boolean isValid(String token) {
        try {
            var username = getSubject(token);
            return userRepository.getByEmail(username).isPresent();
        } catch(ExpiredJwtException e) {
            var claims = e.getClaims();
            throw new ExpiredJwtException(Jwts.header().add("Authorization", "Bearer " + token).build(),
                    claims, "Refresh token expired at " + claims.getExpiration() + ". Re-authentication required!");
        } catch (JwtException | IllegalArgumentException e) {
            throw new MalformedJwtException("The provided JWT token " + token + " is malformed.");
        }
    }
}
