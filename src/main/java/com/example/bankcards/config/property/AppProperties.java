package com.example.bankcards.config.property;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.Duration;

@Getter @Setter
@ConfigurationProperties(prefix = "app")
public class AppProperties {
    private int paginationLimit;

    @ConfigurationProperties(prefix = "app.jwt")
    public record JwtProperties(String secret, Duration tokenExpiration, Duration refreshTokenExpiration) {
    }
}
