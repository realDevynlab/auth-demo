package com.example.authdemo;

import io.micrometer.common.util.StringUtils;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Slf4j
@Configuration
public class TokenValidationFilter extends OncePerRequestFilter {

    private final JwtDecoder jwtDecoder;

    @Autowired
    public TokenValidationFilter(@Lazy JwtDecoder jwtDecoder) {
        this.jwtDecoder = jwtDecoder;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String authorizationHeader = request.getHeader("Authorization");
        if (StringUtils.isNotEmpty(authorizationHeader) && authorizationHeader.startsWith("Bearer ")) {
            String token = authorizationHeader.substring(7);
            try {
                Jwt jwt = jwtDecoder.decode(token);
                String username = jwt.getSubject();
                if (username == null) {
                    log.error("JWT subject is null");
                    UserDetails anonymousUser = User.withUsername("anonymous").password("").build();
                    UsernamePasswordAuthenticationToken anonymousAuthentication = new UsernamePasswordAuthenticationToken(anonymousUser, null, anonymousUser.getAuthorities());
                    SecurityContextHolder.getContext().setAuthentication(anonymousAuthentication);
                } else {
                    List<String> authorities = jwt.getClaim("authorities");
                    UserDetails userDetails;
                    if (authorities == null)
                        userDetails = User.withUsername(username).password("").build();
                    else
                        userDetails = User.withUsername(username).authorities(authorities.toArray(new String[0])).password("").build();
                    UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                }
            } catch (Exception e) {
                log.error("Invalid JWT token: {}", e.getMessage());
            }
        }
        filterChain.doFilter(request, response);
    }

}
