package com.example.bankcards.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jakarta.annotation.Nonnull;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.context.annotation.*;
import org.springframework.context.annotation.aspectj.EnableSpringConfigured;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.instrument.classloading.LoadTimeWeaver;
import org.springframework.instrument.classloading.tomcat.TomcatLoadTimeWeaver;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import static org.springframework.context.annotation.AdviceMode.ASPECTJ;

@Configuration
@EnableAspectJAutoProxy
@EnableLoadTimeWeaving(aspectjWeaving = EnableLoadTimeWeaving.AspectJWeaving.AUTODETECT)
@EnableTransactionManagement(mode=ASPECTJ)
@ConfigurationPropertiesScan
@EnableJpaRepositories("com.example.bankcards.repository")
@EnableSpringConfigured
public class AppConfiguration implements LoadTimeWeavingConfigurer {
    @Override
    @Nonnull
    public LoadTimeWeaver getLoadTimeWeaver() {
        return new TomcatLoadTimeWeaver();
    }

    @Bean
    public ObjectMapper objectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        return objectMapper;
    }
}
