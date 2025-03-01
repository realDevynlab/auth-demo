package com.example.authdemo;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthenticationServiceImpl implements AuthenticationService {

    @Value("${jwt.refresh-expiry}")
    private long jwtRefreshExpiry;

    private final JWTService jwtService;
    private final UserMapper userMapper;
    private final UserService userService;
    private final UserRepository userRepository;
    private final UserDetailsService userDetailsService;
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
        RefreshToken savedRefreshToken = new RefreshToken();
        savedRefreshToken.setToken(refreshToken);
        savedRefreshToken.setUserId(userEntity.getId());
        savedRefreshToken.setExpiryDate(Instant.now().plus(jwtRefreshExpiry, ChronoUnit.SECONDS));
        refreshTokenRepository.save(savedRefreshToken);
        UserDTO userDTO = userMapper.toDTO(userEntity);
        return AuthenticationResponse.builder()
                .user(userDTO)
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

    @Override
    public RefreshTokenResponse refreshToken(String refreshToken) {
        RefreshToken token = refreshTokenRepository.findByToken(refreshToken).orElseThrow(() -> new RuntimeException(refreshToken + " Refresh token is not in database!"));
        if (token.getExpiryDate().isBefore(Instant.now())) {
            refreshTokenRepository.delete(token);
            throw new RuntimeException(token.getToken() + " Refresh token was expired. Please make a new sign-in request");
        }
        UserEntity userEntity = userService.findById(token.getUserId()).orElseThrow(() -> new RuntimeException("User not found"));
        UserDetails userDetails = userDetailsService.loadUserByUsername(userEntity.getUsername());
        Authentication authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
        String accessToken = jwtService.getAccessToken(authentication);
        String newRefreshToken = jwtService.getRefreshToken(authentication);
        return RefreshTokenResponse.builder().accessToken(accessToken).refreshToken(newRefreshToken).build();
    }

}
