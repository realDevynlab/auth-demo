package com.example.authdemo;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthenticationServiceImpl implements AuthenticationService {

    @Value("${jwt.refresh-expiry}")
    private long jwtRefreshExpiry;

    private final UserMapper userMapper;
    private final JWTService jwtService;
    private final UserService userService;
    private final UserRepository userRepository;
    private final AuthenticationManager authenticationManager;
    private final RefreshTokenRepository refreshTokenRepository;

    @Override
    public AuthenticationResponse login(LoginDTO loginDTO) {
        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginDTO.usernameOrEmail(), loginDTO.password()));
        SecurityContextHolder.getContext().setAuthentication(authentication);
        User principal = (User) authentication.getPrincipal();
        String accessToken = jwtService.getAccessToken(authentication);
        String refreshToken = jwtService.getRefreshToken(authentication);
        UserEntity userEntity = userRepository.findByUsername(principal.getUsername()).orElse(userRepository.findByEmail(principal.getUsername()).orElse(null));
        if (userEntity == null) {
            throw new RuntimeException("User not found after authentication");
        }
        UserDTO userDTO = userMapper.toDTO(userEntity);
        return AuthenticationResponse.builder()
                .user(userDTO)
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

    @Override
    public RefreshToken createRefreshToken(UUID userId) {
        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setUserId(userId);
        refreshToken.setExpiryDate(Instant.now().plusSeconds(jwtRefreshExpiry));
        refreshToken.setToken(UUID.randomUUID().toString());
        refreshToken = refreshTokenRepository.save(refreshToken);
        return refreshToken;
    }

    @Override
    public RefreshTokenResponse refreshToken(String refreshToken) {
        return findByToken(refreshToken)
                .map(this::verifyExpiration)
                .map(RefreshToken::getUserId)
                .map(userId -> userService.findById(userId)
                        .map(user -> {
                            Authentication authentication = new UsernamePasswordAuthenticationToken(user.getUsername(), null, user.getAuthorities());
                            String accessToken = jwtService.getAccessToken(authentication);
                            String newRefreshToken = jwtService.getRefreshToken(authentication);
                            return RefreshTokenResponse.builder().accessToken(accessToken).refreshToken(newRefreshToken).build();
                        })
                        .orElseThrow(() -> new RuntimeException("User not found")))
                .orElseThrow(() -> new RuntimeException(refreshToken + " Refresh token is not in database!"));
    }

    @Override
    public Optional<RefreshToken> findByToken(String token) {
        return refreshTokenRepository.findByToken(token);
    }

    @Override
    public RefreshToken verifyExpiration(RefreshToken token) {
        if (token.getExpiryDate().isBefore(Instant.now())) {
            refreshTokenRepository.delete(token);
            throw new RuntimeException(token.getToken() + " Refresh token was expired. Please make a new sign-in request");
        }
        return token;
    }

    @Transactional
    public void deleteByUserId(UUID userId) {
        refreshTokenRepository.deleteByUserId(userId);
    }

}
