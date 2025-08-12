package com.example.bankcards.security.filter;

import com.example.bankcards.dto.UserDto;
import com.example.bankcards.dto.RefreshTokenDto;
import com.example.bankcards.entity.User;
import com.example.bankcards.service.RefreshTokenService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.ws.rs.core.HttpHeaders;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtLoginFilter extends UsernamePasswordAuthenticationFilter {
    private final RefreshTokenService refreshTokenService;
    private final ObjectMapper mapper = new ObjectMapper();

    @Override
    @Autowired
    public void setAuthenticationManager(AuthenticationManager authenticationManager) {
        super.setAuthenticationManager(authenticationManager);
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
            throws AuthenticationException {
        assert request != null;

        try {
            var authModel = mapper.readValue(request.getInputStream(), UserDto.class);
            var authToken = new UsernamePasswordAuthenticationToken(authModel.getEmail(), authModel.getPassword());
            return getAuthenticationManager().authenticate(authToken);
        } catch (IOException e) {
            throw new AuthenticationServiceException("Cannot set user authentication: " + e.getMessage());
        }
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain,
                                            Authentication authResult) throws IOException {
        var user = (User) authResult.getPrincipal();
        var refreshToken = refreshTokenService.create(user.getUsername());
        var responseModel = RefreshTokenDto.builder()
                .accessToken(refreshToken.getAccessToken())
                .build();
        response.addHeader("X-Custom-Security-Header", String.format("Bearer %s", refreshToken.getAccessToken()));
        response.addHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
        response.getOutputStream().write(mapper.writeValueAsBytes(responseModel));
    }
}
