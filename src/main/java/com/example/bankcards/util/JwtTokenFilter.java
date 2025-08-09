package com.example.bankcards.util;

import com.example.bankcards.service.JwtService;
import com.example.bankcards.service.UserService;
import jakarta.annotation.Nullable;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.ws.rs.core.HttpHeaders;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtTokenFilter extends OncePerRequestFilter {
    private final JwtService jwtService;
    private final UserService userService;

    @Override
    protected void doFilterInternal(@Nullable HttpServletRequest request, @Nullable HttpServletResponse response,
                                    @Nullable FilterChain filterChain)
            throws ServletException, IOException {
        try {
            assert request != null;
            var jwtToken = getToken(request);

            if(jwtService.validate(jwtToken)) {
                var username = jwtService.getUserDetails(jwtToken).getUsername();
                var userDetails = userService.loadUserByUsername(username);

                var authToken = new UsernamePasswordAuthenticationToken(
                        username, userDetails.getPassword(), userDetails.getAuthorities());
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        } catch (Exception e) {
            log.error("Cannot set user authentication: {}", e.getMessage());
        }

        assert filterChain != null;
        filterChain.doFilter(request,response);
    }

    public String getToken(HttpServletRequest request) {
        String headerAuth = request.getHeader(HttpHeaders.AUTHORIZATION);

        if(StringUtils.hasText(headerAuth) && headerAuth.startsWith("Bearer ")) {
            return headerAuth.substring(7);
        }
        return null;
    }
}
