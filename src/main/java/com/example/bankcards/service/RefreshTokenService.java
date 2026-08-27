package com.example.bankcards.service;

import com.example.bankcards.dto.RefreshTokenDto;
import com.example.bankcards.entity.RefreshToken;
import com.example.bankcards.entity.User;
import com.example.bankcards.repository.TokenRepository;
import com.example.bankcards.security.JwtService;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

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
     * @param refreshTokenId   token database record id
     *
     * @return  JWT token database record representation object
     */
    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.REPEATABLE_READ)
    public RefreshTokenDto create(UUID refreshTokenId) {
        Instant issueDate = Instant.now();
        Instant expireDate = issueDate.plusMillis(refreshTokenExpiration.toMillis() * Timer.ONE_MINUTE);
        String accessToken = jwtService.create(refreshTokenId);
        RefreshToken refreshToken = tokenRepository.save(new RefreshToken(refreshTokenId, accessToken, issueDate, expireDate));
        return RefreshTokenDto.builder().accessToken(refreshToken.getToken()).build();
    }

    /**
     * Requests JWT token database record update.
     * Main JWT token database record update method.
     * @param username   email address of a user requested JWT token update
     *
     * @return  JWT token database record representation object
     */
    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.REPEATABLE_READ)
    public RefreshTokenDto update(String username) {
        User user = userService.loadUserByUsername(username);
        return create(UUID.fromString(validate(find(user.getId()))));
    }

    /**
     * Requests JWT token database record deletion.
     * Main JWT token database record delete method.
     * @param refreshTokenId   token database record id
     *
     */
    public void delete(UUID refreshTokenId) {
        tokenRepository.deleteById(refreshTokenId);
    }

    /**
     * Removes expired records from the token database.
     * Main user JWT token clean up method.
     *
     */
    @Scheduled(fixedRate = 300_000)
    public void purgeExpiredTokens() {
        tokenRepository.deleteExpiredTokens(Instant.now());
    }

    /**
     * Requests JWT token database record matching refresh token id.
     * Supporting JWT token database record find method.
     * @param refreshTokenId   token database record id
     *
     * @return  JWT token database record
     */
    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.REPEATABLE_READ)
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
    private String validate(RefreshToken token) throws ExpiredJwtException {
        String refreshTokenId = jwtService.getClaims(token.getToken()).getSubject();
        if(token.getExpireDate().compareTo(Instant.now()) < 0) {
            delete(UUID.fromString(refreshTokenId));
            throw new ExpiredJwtException(Jwts.header()
                    .add("Authorization", "Bearer " + token.getToken())
                    .build(), Jwts.claims()
                    .id(UUID.randomUUID().toString())
                    .subject(token.getId().toString())
                    .expiration(Date.from(token.getExpireDate()))
                    .issuedAt(Date.from(token.getIssueDate())).build(), "Refresh token was expired. Repeat login procedure!");
        }
        return refreshTokenId;
    }
}
