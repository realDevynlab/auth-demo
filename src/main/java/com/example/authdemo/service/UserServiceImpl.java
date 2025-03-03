package com.example.authdemo.service;

import com.example.authdemo.dto.UserDTO;
import com.example.authdemo.entity.RoleEntity;
import com.example.authdemo.entity.UserEntity;
import com.example.authdemo.exception.NotFoundException;
import com.example.authdemo.mapper.UserMapper;
import com.example.authdemo.model.RoleName;
import com.example.authdemo.model.SignupRequest;
import com.example.authdemo.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserMapper userMapper;
    private final RoleService roleService;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public UserDTO signup(SignupRequest signupRequest) {
        try {
            UserEntity userEntity = userMapper.toEntity(signupRequest);
            String encodedPassword = passwordEncoder.encode(userEntity.getPassword());
            userEntity.setPassword(encodedPassword);
            RoleEntity defaultRole = roleService.findByName(RoleName.USER);
            userEntity.addRole(defaultRole);
            userEntity = userRepository.save(userEntity);
            return userMapper.toDTO(userEntity);
        } catch (DataIntegrityViolationException e) {
            throw new DataIntegrityViolationException("Username or Email already taken.");
        }
    }

    @Override
    public UserEntity findByUsername(String username) {
        return userRepository.findByUsername(username).orElseThrow(() -> new NotFoundException("User not found"));
    }

    @Override
    public void createUser(UserEntity user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userRepository.save(user);
    }

    @Override
    public boolean userExists(String username) {
        return userRepository.findByUsername(username).isPresent();
    }

}
