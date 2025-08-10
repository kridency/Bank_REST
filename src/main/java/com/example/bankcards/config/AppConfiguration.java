package com.example.bankcards.config;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.aspectj.EnableSpringConfigured;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import static org.springframework.context.annotation.AdviceMode.ASPECTJ;

@Configuration
@EnableAspectJAutoProxy
@EnableTransactionManagement(mode=ASPECTJ)
@ConfigurationPropertiesScan
@EnableJpaRepositories("com.example.bankcards.repository")
@EnableSpringConfigured
@RequiredArgsConstructor
public class AppConfiguration {
}
