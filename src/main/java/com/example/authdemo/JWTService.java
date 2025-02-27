package com.example.authdemo;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;

@Service
@RequiredArgsConstructor
public class JWTService {

    @Value("${jwt.access-expiry}")
    long jwtAccessExpiry;

    @Value("${jwt.refresh-expiry}")
    long jwtRefreshExpiry;

    private final JwtEncoder jwtEncoder;

    public String getAccessToken(Authentication authentication) {
        Instant now = Instant.now();
        List<String> authorities = authentication.getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .toList();
        Consumer<Map<String, Object>> claimsConsumer = claims -> {
            claims.put("jti", UUID.randomUUID().toString());
            claims.put("aud", "http://localhost:3000");
            claims.put("token_type", "Access Token");
            claims.put("authorities", authorities);
        };
        JwtClaimsSet jwtClaimsSet = JwtClaimsSet.builder()
                .issuer("http://127.0.0.1:8080")
                .issuedAt(now)
                .expiresAt(now.plus(jwtAccessExpiry, ChronoUnit.SECONDS))
                .subject(authentication.getName())
                .claims(claimsConsumer)
                .build();
        return jwtEncoder.encode(JwtEncoderParameters.from(jwtClaimsSet)).getTokenValue();
    }

    public String getRefreshToken(Authentication authentication) {
        Instant now = Instant.now();
        Consumer<Map<String, Object>> consumer = claims -> {
            claims.put("jti", UUID.randomUUID().toString());
            claims.put("token_type", "Refresh Token");
        };
        JwtClaimsSet jwtClaimsSet = JwtClaimsSet.builder()
                .issuer("http://127.0.0.1:8080")
                .issuedAt(now)
                .expiresAt(now.plus(jwtRefreshExpiry, ChronoUnit.SECONDS))
                .subject(authentication.getName())
                .claims(consumer)
                .build();
        return jwtEncoder.encode(JwtEncoderParameters.from(jwtClaimsSet)).getTokenValue();
    }

}
