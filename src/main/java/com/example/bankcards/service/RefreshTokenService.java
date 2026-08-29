package com.example.bankcards.service;

import com.example.bankcards.dto.RefreshTokenDto;
import com.example.bankcards.entity.RefreshToken;
import com.example.bankcards.repository.TokenRepository;
import com.example.bankcards.security.JwtService;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.management.timer.Timer;
import java.time.Instant;
import java.util.Date;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {
    @Value("${app.security.refreshTokenExpiration}")
    private long refreshTokenExpiration;

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
        Instant expireDate = issueDate.plusMillis(refreshTokenExpiration * Timer.ONE_MINUTE);
        String accessToken = jwtService.create(refreshTokenId);
        RefreshToken refreshToken = tokenRepository.save(new RefreshToken(refreshTokenId, accessToken, issueDate, expireDate));
        return RefreshTokenDto.builder().accessToken(refreshToken.getToken()).build();
    }

    /**
     * Requests JWT token database record update.
     * Main JWT token database record update method.
     * @param refreshTokenId   email address of a user requested JWT token update
     *
     * @return  JWT token database record representation object
     */
    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.REPEATABLE_READ)
    public RefreshTokenDto update(UUID refreshTokenId) {
        if (isValid(refreshTokenId)) {
            return create(refreshTokenId);
        } else {
            return null;
        }
    }

    /**
     * Requests JWT token database record deletion.
     * Main JWT token database record delete method.
     *
     */
    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.REPEATABLE_READ)
    @Scheduled(fixedRateString = "${app.security.refreshTokenExpiration}")
    public void delete() {
        tokenRepository.deleteExpiredTokens(Instant.now());
    }

    /**
     * Requests JWT token database record matching refresh token id.
     * Supporting JWT token database record find method.
     * @param refreshTokenId   token database record id
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
     * @param refreshTokenId   token database record id
     *
     * @return  JWT token database record
     */
    private boolean isValid(UUID refreshTokenId) throws ExpiredJwtException, MalformedJwtException {
        RefreshToken refreshToken = find(refreshTokenId);
        if(refreshToken.getExpireDate().compareTo(Instant.now()) < 0) {
            throw new ExpiredJwtException(Jwts.header()
                    .add("Authorization", "Bearer " + refreshToken.getToken()).build(),
                    Jwts.claims()
                            .id(refreshToken.getId().toString())
                            .subject(refreshToken.getToken())
                            .expiration(Date.from(refreshToken.getExpireDate()))
                            .issuedAt(Date.from(refreshToken.getIssueDate())).build(),
                    "Refresh token expired. Re-authentication required!");
        }
        return true;
    }
}
