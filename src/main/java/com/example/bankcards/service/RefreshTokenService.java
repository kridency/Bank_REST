package com.example.bankcards.service;

import com.example.bankcards.dto.RefreshTokenDto;
import com.example.bankcards.entity.RefreshToken;
import com.example.bankcards.entity.User;
import com.example.bankcards.repository.TokenRepository;
import com.example.bankcards.security.UserService;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.management.timer.Timer;
import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {
    @Value("${app.security.refreshTokenExpiration}")
    private Duration refreshTokenExpiration;

    private final UserService userService;
    private final JwtService jwtService;
    private final TokenRepository tokenRepository;

    /**
     * Request JWT token database to create new record.
     * Main JWT token database record creation method.
     * @param username   email address of a user requested authentication
     *
     * @return  JWT token database record representation object
     */
    public RefreshTokenDto create(String username) {
        User user = userService.find(username);
        Instant issueDate = Instant.now();
        Instant expireDate = issueDate.plusMillis(refreshTokenExpiration.toMillis() * Timer.ONE_MINUTE);
        String accessToken = jwtService.create(username);
        RefreshToken token = new RefreshToken(user.getId(), accessToken, issueDate, expireDate);
        tokenRepository.save(token);
        return RefreshTokenDto.builder().accessToken(accessToken).build();
    }

    /**
     * Requests JWT token database record update.
     * Main JWT token database record update method.
     * @param username   email address of a user requested JWT token update
     *
     * @return  JWT token database record representation object
     */
    public RefreshTokenDto update(String username) {
        User user = userService.find(username);
        String accessToken = validate(find(user.getId())).getToken();
        return create(jwtService.find(accessToken).getUsername());
    }

    /**
     * Requests JWT token database record deletion.
     * Main JWT token database record delete method.
     * @param accessToken   JWT token of a user requested active session completion
     *
     */
    public void delete(String accessToken) {
        tokenRepository.deleteByToken(accessToken);
    }

    /**
     * Requests JWT token database record matching refresh token id.
     * Supporting JWT token database record find method.
     * @param refreshTokenId   идентификатор записи базы данных электронных пропусков
     *
     * @return  JWT token database record
     */
    private RefreshToken find(UUID refreshTokenId) {
        return tokenRepository.findById(refreshTokenId)
                .orElseThrow(() -> new EntityNotFoundException(" Refresh token " + refreshTokenId + " not found "));
    }

    /**
     * Validates user JWT token.
     * Supporting user JWT token validation method.
     * @param token   JWT token database record
     *
     * @return  JWT token database record
     */
    public RefreshToken validate(RefreshToken token) {
        String username = jwtService.find(token.getToken()).getUsername();
        if(token.getExpireDate().compareTo(Instant.now()) < 0) {
            delete(token.getToken());
            throw new ExpiredJwtException(Jwts.header()
                    .add("Authorization", "Bearer " + token.getToken())
                    .build(), Jwts.claims()
                    .id(String.valueOf(token.getId()))
                    .subject(username)
                    .expiration(Date.from(token.getExpireDate()))
                    .issuedAt(Date.from(token.getIssueDate())).build(), "Refresh token was expired. Repeat login procedure!");
        }

        return token;
    }
}
