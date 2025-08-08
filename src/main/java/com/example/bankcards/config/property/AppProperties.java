package com.example.bankcards.config.property;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.Duration;

@ConfigurationProperties(prefix = "app")
public class AppProperties {
    @ConfigurationProperties(prefix = "app.jwt")
    public record JwtProperties(String secret, Duration tokenExpiration, Duration refreshTokenExpiration) {
    }
}
