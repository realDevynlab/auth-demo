package com.example.authdemo;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Slf4j
@Service
public class JWTService {

    @Value("${jwt.issuer}")
    private String jwtIssuer;

    @Value("${jwt.audience}")
    private String jwtAudience;

    @Value("${jwt.access-expiry}")
    private long jwtAccessExpiry;

    @Value("${jwt.refresh-expiry}")
    private long jwtRefreshExpiry;

    private final JwtEncoder jwtEncoder;
    private final UserService userService;
    private final StringRedisTemplate redisTemplate;

    @Autowired
    public JWTService(JwtEncoder jwtEncoder, UserService userService, StringRedisTemplate redisTemplate) {
        this.jwtEncoder = jwtEncoder;
        this.userService = userService;
        this.redisTemplate = redisTemplate;
    }

    public String getAccessToken(Authentication authentication) {
        Instant now = Instant.now();
        List<String> authorities = authentication.getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());
        User principal = (User) authentication.getPrincipal();
        JwtClaimsSet jwtClaimsSet = JwtClaimsSet.builder()
                .issuer(jwtIssuer)
                .issuedAt(now)
                .expiresAt(now.plus(jwtAccessExpiry, ChronoUnit.SECONDS))
                .subject(principal.getUsername())
                .claims(claims -> {
                    claims.put("jti", UUID.randomUUID().toString());
                    claims.put("aud", jwtAudience);
                    claims.put("token_type", "Access Token");
                    claims.put("scope", authorities);
                })
                .build();
        return jwtEncoder.encode(JwtEncoderParameters.from(jwtClaimsSet)).getTokenValue();
    }

    public String getRefreshToken(Authentication authentication) {
        Instant now = Instant.now();
        String jti = UUID.randomUUID().toString();
        User principal = (User) authentication.getPrincipal();
        UserEntity userEntity = userService.findByUsername(principal.getUsername());
        JwtClaimsSet jwtClaimsSet = JwtClaimsSet.builder()
                .issuer(jwtIssuer)
                .issuedAt(now)
                .expiresAt(now.plus(jwtRefreshExpiry, ChronoUnit.SECONDS))
                .subject(authentication.getName())
                .claims(claims -> {
                    claims.put("jti", jti);
                    claims.put("token_type", "Refresh Token");
                })
                .build();
        String refreshToken = jwtEncoder.encode(JwtEncoderParameters.from(jwtClaimsSet)).getTokenValue();
        String redisKey = "refresh_token:" + userEntity.getId().toString() + ":" + jti;
        redisTemplate.opsForValue().set(redisKey, refreshToken, jwtRefreshExpiry, TimeUnit.SECONDS);
        return refreshToken;
    }

}
