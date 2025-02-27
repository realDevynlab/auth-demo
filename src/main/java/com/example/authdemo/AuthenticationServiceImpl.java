package com.example.authdemo;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthenticationServiceImpl implements AuthenticationService {

    private final JWTService jwtService;
    private final UserRepository userRepository;
    private final AuthenticationManager authenticationManager;

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
        UserDTO userDTO = UserDTO.builder().username(userEntity.getUsername()).email(userEntity.getEmail()).build();
        return AuthenticationResponse.builder()
                .user(userDTO)
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

}
