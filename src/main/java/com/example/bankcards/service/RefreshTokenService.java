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
    @Value("${app.jwt.refreshTokenExpiration}")
    private Duration refreshTokenExpiration;

    private final UserService userService;
    private final JwtService jwtService;
    private final TokenRepository tokenRepository;

    /**
     * Запускает обращение к базе данных электронных пропусков для создания новой записи.
     * Основной метод для создания записи электронного пропуска в базе данных.
     * @param username   адрес электронной почты пользователя, отправившего запрос на аутентификацию
     *
     * @return  объект описания результата обращения к базе данных электронных пропусков
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
     * Запускает обращение к базе данных электронных пропусков для обновления записи.
     * Основной метод для обновления записи электронного пропуска в базе данных.
     * @param username   адрес электронной почты пользователя, отправившего запрос на обновление электронного пропуска
     *
     * @return  объект описания результата обращения к базе данных электронных пропусков
     */
    public RefreshTokenDto update(String username) {
        User user = userService.find(username);
        String accessToken = validate(find(user.getId())).getToken();
        return create(jwtService.find(accessToken).getUsername());
    }

    /**
     * Запускает обращение к базе данных электронных пропусков для удаления записи.
     * Основной метод для удаления записи электронного пропуска в базе данных.
     * @param accessToken   электронный пропуск пользователя, отправившего запрос на завершение активной сессии
     *
     */
    public void delete(String accessToken) {
        tokenRepository.deleteByToken(accessToken);
    }

    /**
     * Проверяет электронный пропуск пользователя на валидность.
     * Вспомогательный метод для проверки валидности электронного пропуска пользователя.
     * @param token   объект отображающий запись базы данных электронных пропусков
     *
     * @return  объект описания результата обращения к базе данных электронных пропусков
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

    /**
     * Проверяет электронный пропуск пользователя на валидность.
     * Вспомогательный метод для получения записи базы данных электронных пропусков.
     * @param refreshTokenId   идентификатор записи базы данных электронных пропусков
     *
     * @return  объект описания результата обращения к базе данных электронных пропусков
     */
    private RefreshToken find(UUID refreshTokenId) {
        return tokenRepository.findById(refreshTokenId)
                .orElseThrow(() -> new EntityNotFoundException(" Refresh token " + refreshTokenId + " not found "));
    }
}
