package com.example.bankcards.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jakarta.annotation.Nonnull;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.*;
import org.springframework.context.annotation.aspectj.EnableSpringConfigured;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.instrument.classloading.InstrumentationLoadTimeWeaver;
import org.springframework.instrument.classloading.LoadTimeWeaver;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@EnableAspectJAutoProxy(proxyTargetClass = true)
@EnableLoadTimeWeaving(aspectjWeaving = EnableLoadTimeWeaving.AspectJWeaving.AUTODETECT)
@EnableTransactionManagement(mode = AdviceMode.ASPECTJ)
@ConfigurationPropertiesScan
@EnableJpaRepositories("com.example.bankcards.repository")
@EnableJpaAuditing
@EnableAutoConfiguration
@EnableSpringConfigured
@EnableScheduling
public class ApplicationConfiguration implements LoadTimeWeavingConfigurer {
    private final InstrumentationLoadTimeWeaver loadTimeWeaver;

    public ApplicationConfiguration(ApplicationContext applicationContext) {
        this.loadTimeWeaver =
                new InstrumentationLoadTimeWeaver(applicationContext.getClassLoader());
    }

    @Override
    @Nonnull
    public LoadTimeWeaver getLoadTimeWeaver() { return loadTimeWeaver; }

    @Bean
    public ObjectMapper objectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        return objectMapper;
    }
}
