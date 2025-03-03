package com.example.authdemo.service;

import com.example.authdemo.dto.UserDTO;
import com.example.authdemo.entity.UserEntity;
import com.example.authdemo.exception.UnauthorizedException;
import com.example.authdemo.mapper.AuthenticationMapper;
import com.example.authdemo.model.AuthenticationResponse;
import com.example.authdemo.model.LoginRequest;
import com.example.authdemo.model.RefreshTokenResponse;
import com.example.authdemo.util.JWTService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.stereotype.Service;

import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthenticationServiceImpl implements AuthenticationService {

    private final JwtDecoder jwtDecoder;
    private final JWTService jwtService;
    private final UserService userService;
    private final StringRedisTemplate redisTemplate;
    private final UserDetailsService userDetailsService;
    private final AuthenticationMapper authenticationMapper;
    private final AuthenticationManager authenticationManager;

    @Override
    public AuthenticationResponse login(LoginRequest loginRequest) {
        try {
            Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginRequest.usernameOrEmail(), loginRequest.password()));
            SecurityContextHolder.getContext().setAuthentication(authentication);
            User principal = (User) authentication.getPrincipal();
            UserEntity userEntity = userService.findByUsername(principal.getUsername());
            Set<String> keys = redisTemplate.keys("refresh_token:" + userEntity.getId().toString() + ":*");
            if (!keys.isEmpty()) redisTemplate.delete(keys);
            String accessToken = jwtService.getAccessToken(authentication);
            String refreshToken = jwtService.getRefreshToken(authentication);
            UserDTO userDTO = authenticationMapper.toDTO(userEntity);
            return AuthenticationResponse.builder().user(userDTO).accessToken(accessToken).refreshToken(refreshToken).build();
        } catch (BadCredentialsException e) {
            throw new UnauthorizedException(e.getMessage());
        }
    }

    @Override
    public RefreshTokenResponse refreshToken(String refreshToken) {
        try {
            Jwt refreshTokenJwt = jwtDecoder.decode(refreshToken);
            String jti = refreshTokenJwt.getClaimAsString("jti");
            String username = refreshTokenJwt.getSubject();
            UserEntity userEntity = userService.findByUsername(username);
            String redisKey = "refresh_token:" + userEntity.getId().toString() + ":" + jti;
            if (!redisTemplate.hasKey(redisKey)) throw new JwtException("Refresh token has been revoked or used.");
            UserDetails userDetails = userDetailsService.loadUserByUsername(userEntity.getUsername());
            Authentication authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
            String accessToken = jwtService.getAccessToken(authentication);
            String newRefreshToken = jwtService.getRefreshToken(authentication);
            redisTemplate.delete(redisKey);
            return RefreshTokenResponse.builder().accessToken(accessToken).refreshToken(newRefreshToken).build();
        } catch (JwtException e) {
            throw new UnauthorizedException("Invalid refresh token.");
        }
    }

}
