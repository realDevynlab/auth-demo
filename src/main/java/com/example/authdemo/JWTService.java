package com.example.authdemo;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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
    private final UserRepository userRepository;

    @Autowired
    public JWTService(JwtEncoder jwtEncoder, UserRepository userRepository) {
        this.jwtEncoder = jwtEncoder;
        this.userRepository = userRepository;
    }

    public String getAccessToken(Authentication authentication) {
        Instant now = Instant.now();
        List<String> authorities = authentication.getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());
        User principal = (User) authentication.getPrincipal();
        UserEntity userEntity = userRepository.findByUsername(principal.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found after authentication"));
        JwtClaimsSet jwtClaimsSet = JwtClaimsSet.builder()
                .issuer(jwtIssuer)
                .issuedAt(now)
                .expiresAt(now.plus(jwtAccessExpiry, ChronoUnit.SECONDS))
                .subject(authentication.getName())
                .claims(claims -> {
                    claims.put("jti", UUID.randomUUID().toString());
                    claims.put("aud", jwtAudience);
                    claims.put("token_type", "Access Token");
                    claims.put("authorities", authorities);
                    claims.put("userId", userEntity.getId().toString());
                })
                .build();
        return jwtEncoder.encode(JwtEncoderParameters.from(jwtClaimsSet)).getTokenValue();
    }

    public String getRefreshToken(Authentication authentication) {
        Instant now = Instant.now();
        JwtClaimsSet jwtClaimsSet = JwtClaimsSet.builder()
                .issuer(jwtIssuer)
                .issuedAt(now)
                .expiresAt(now.plus(jwtRefreshExpiry, ChronoUnit.SECONDS))
                .subject(authentication.getName())
                .claims(claims -> {
                    claims.put("jti", UUID.randomUUID().toString());
                    claims.put("token_type", "Refresh Token");
                })
                .build();
        return jwtEncoder.encode(JwtEncoderParameters.from(jwtClaimsSet)).getTokenValue();
    }

}
