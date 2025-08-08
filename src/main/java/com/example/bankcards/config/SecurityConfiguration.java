package com.example.bankcards.config;

import com.example.bankcards.mapper.UserMapper;
import com.example.bankcards.util.JwtLoginFilter;
import com.example.bankcards.service.UserService;
import com.example.bankcards.util.JwtAuthEntryPoint;
import com.example.bankcards.util.JwtTokenFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.aspectj.EnableSpringConfigured;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.security.web.servletapi.SecurityContextHolderAwareRequestFilter;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;


@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfiguration {
    private final UserService userService;
    private final JwtAuthEntryPoint jwtAuthEntryPoint;
    private final JwtTokenFilter jwtTokenFilter;

    @Bean
    public AuthenticationManager authenticationManager() {
        DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();
        authenticationProvider.setUserDetailsService(userService);
        authenticationProvider.setPasswordEncoder(UserMapper.encoder);
        return new ProviderManager(authenticationProvider);
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http, JwtLoginFilter jwtLoginFilter) throws Exception {
        jwtLoginFilter.setFilterProcessesUrl("/api/v1/auth/login");

        http
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers("/api/v1/auth/logout").authenticated()
                        .requestMatchers("/api/v1/auth/change-password-link").authenticated()
                        .anyRequest().permitAll())
                .exceptionHandling(exception -> exception.authenticationEntryPoint(jwtAuthEntryPoint))
                .cors(Customizer.withDefaults())
                .csrf(AbstractHttpConfigurer::disable)
                .securityContext(securityContext ->
                        securityContext.securityContextRepository(new HttpSessionSecurityContextRepository()))
                .sessionManagement(configurer ->
                        configurer.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .formLogin(AbstractHttpConfigurer::disable)
                .userDetailsService(userService)
                .authenticationManager(authenticationManager())
                .addFilterBefore(jwtTokenFilter, UsernamePasswordAuthenticationFilter.class)
                .addFilterAfter(jwtLoginFilter, SecurityContextHolderAwareRequestFilter.class);

        return http.build();
    }
}
