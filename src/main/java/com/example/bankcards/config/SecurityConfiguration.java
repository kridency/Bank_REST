package com.example.bankcards.config;

import com.example.bankcards.security.filter.JwtLoginFilter;
import com.example.bankcards.security.JwtAuthEntryPoint;
import com.example.bankcards.security.filter.JwtTokenFilter;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.security.web.servletapi.SecurityContextHolderAwareRequestFilter;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import javax.crypto.SecretKey;
import java.io.InputStream;
import java.security.KeyStore;
import java.util.List;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
@OpenAPIDefinition(info = @Info(title = "Banking Card API", version = "v1"))
@SecurityScheme(
        name = "bearerAuth",
        type = SecuritySchemeType.HTTP,
        bearerFormat = "JWT",
        scheme = "bearer"
)
public class SecurityConfiguration {
    @Bean
    public JwtAuthEntryPoint jwtAuthEntryPoint(ObjectMapper objectMapper) {
        return new JwtAuthEntryPoint(objectMapper);
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(UserDetailsService userService) {
        DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider(userService);
        authenticationProvider.setPasswordEncoder(passwordEncoder());
        return new ProviderManager(authenticationProvider);
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        final CorsConfiguration configuration = new CorsConfiguration().applyPermitDefaultValues();
        configuration.setAllowedOriginPatterns(List.of("*"));
        configuration.setAllowedMethods(List.of("*"));
        configuration.setAllowCredentials(true);

        var source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http, JwtLoginFilter jwtLoginFilter,
                                           JwtTokenFilter jwtTokenFilter, ObjectMapper objectMapper) throws Exception {
        jwtLoginFilter.setFilterProcessesUrl("/api/login");

        http
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers("/api/login","/openapi-docs", "/openapi-docs/**", "/swagger-ui/**",
                                "/proxy/**", "/favicon.ico", "/error").permitAll()
                        .anyRequest().authenticated())
                .exceptionHandling(exception ->
                        exception.authenticationEntryPoint(jwtAuthEntryPoint(objectMapper)))
                .cors(configurer -> configurer.configurationSource(corsConfigurationSource()))
                .csrf(AbstractHttpConfigurer::disable)
                .securityContext(securityContext ->
                        securityContext.securityContextRepository(new HttpSessionSecurityContextRepository()))
                .sessionManagement(configurer ->
                        configurer.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .formLogin(AbstractHttpConfigurer::disable)
                .addFilterBefore(jwtTokenFilter, UsernamePasswordAuthenticationFilter.class)
                .addFilterAfter(jwtLoginFilter, SecurityContextHolderAwareRequestFilter.class);

        return http.build();
    }

    @Bean
    @Qualifier("dataEncryptionKey")
    public SecretKey dataEncryptionKey(
            @Value("${app.security.data-keystore.location}") Resource keystoreLocation,
            @Value("${app.security.data-keystore.password}") String keystorePassword,
            @Value("${app.security.data-keystore.key-alias}") String keyAlias,
            @Value("${app.security.data-keystore.type}") String storeType) throws Exception {
        return loadSecretKey(keystoreLocation, keystorePassword, keyAlias, storeType);
    }

    @Bean
    @Qualifier("jwtSigningKey")
    public SecretKey jwtSigningKey(
            @Value("${app.security.jwt-keystore.location}") Resource keystoreLocation,
            @Value("${app.security.jwt-keystore.password}") String keystorePassword,
            @Value("${app.security.jwt-keystore.key-alias}") String keyAlias,
            @Value("${app.security.jwt-keystore.type}") String storeType) throws Exception {
        return loadSecretKey(keystoreLocation, keystorePassword, keyAlias, storeType);
    }

    private SecretKey loadSecretKey(Resource location, String password, String alias, String storeType) throws Exception {
        KeyStore keyStore = KeyStore.getInstance(storeType);

        try (InputStream stream = location.getInputStream()) {
            keyStore.load(stream, password.toCharArray());
        }

        KeyStore.ProtectionParameter protectionParam = new KeyStore.PasswordProtection(password.toCharArray());
        KeyStore.SecretKeyEntry secretKeyEntry = (KeyStore.SecretKeyEntry) keyStore.getEntry(alias, protectionParam);

        if (secretKeyEntry == null) {
            throw new IllegalArgumentException(String.format("Key alias '%s' not found in keystore at %s", alias, location.getDescription()));
        }

        return secretKeyEntry.getSecretKey();
    }
}
