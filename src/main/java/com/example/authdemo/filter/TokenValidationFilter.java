package com.example.authdemo.filter;

import com.example.authdemo.model.APIResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.micrometer.common.util.StringUtils;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.util.MimeTypeUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@Slf4j
@Configuration
public class TokenValidationFilter extends OncePerRequestFilter {

    @Value("${jwt.issuer}")
    private String jwtIssuer;

    @Value("${jwt.audience}")
    private String jwtAudience;

    private final JwtDecoder jwtDecoder;

    @Autowired
    public TokenValidationFilter(@Lazy JwtDecoder jwtDecoder) {
        this.jwtDecoder = jwtDecoder;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String authorizationHeader = request.getHeader("Authorization");
        if (StringUtils.isNotEmpty(authorizationHeader) && authorizationHeader.startsWith("Bearer ")) {
            String token = authorizationHeader.substring("Bearer ".length());
            try {
                Jwt jwt = jwtDecoder.decode(token);
                if (jwt.getSubject() == null) {
                    log.error("JWT subject is missing");
                    handleInvalidToken(response, request.getRequestURI(), "JWT subject is missing");
                } else {
                    String issuer = String.valueOf(jwt.getIssuer());
                    List<String> audiences = jwt.getClaim("aud");
                    List<String> authorities = jwt.getClaim("scope");
                    if (!jwtIssuer.equals(issuer) || audiences == null || !audiences.contains(jwtAudience)) {
                        log.error("JWT issuer or audience is invalid");
                        handleInvalidToken(response, request.getRequestURI(), "JWT issuer or audience is invalid");
                        return;
                    } else if (authorities == null || authorities.isEmpty()) {
                        log.error("JWT authorities are missing");
                        handleInvalidToken(response, request.getRequestURI(), "JWT authorities are missing");
                    } else {
                        UserDetails userDetails = User.withUsername(jwt.getSubject()).authorities(authorities.toArray(new String[0])).password("").build();
                        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                        SecurityContextHolder.getContext().setAuthentication(authentication);
                    }
                }
            } catch (JwtException e) {
                log.error("Invalid JWT token: {}", e.getMessage());
                handleInvalidToken(response, request.getRequestURI(), e.getMessage());
            }
        }
        filterChain.doFilter(request, response);
    }

    private void handleInvalidToken(HttpServletResponse response, String requestURI, String message) throws IOException {
        APIResponse<Map<String, Object>> apiResponse = APIResponse.<Map<String, Object>>builder()
                .status(HttpStatus.UNAUTHORIZED)
                .statusCode(HttpStatus.UNAUTHORIZED.value())
                .message(message)
                .path(requestURI)
                .build();
        ObjectMapper mapper = new ObjectMapper();
        String jsonResponse = mapper.writeValueAsString(apiResponse);
        response.setContentType(MimeTypeUtils.APPLICATION_JSON_VALUE);
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.getOutputStream().println(jsonResponse);
    }

}
