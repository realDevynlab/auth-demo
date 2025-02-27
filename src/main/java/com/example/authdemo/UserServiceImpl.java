package com.example.authdemo;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserMapper userMapper;
    private final RoleService roleService;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public UserDTO signup(SignupDTO signupDTO) {
        UserEntity userEntity = userMapper.toEntity(signupDTO);
        String encodedPassword = passwordEncoder.encode(userEntity.getPassword());
        userEntity.setPassword(encodedPassword);
        RoleEntity defaultRole = roleService.findRoleByName(RoleName.USER.name()).orElse(null);
        userEntity.addRole(defaultRole);
        userEntity = userRepository.save(userEntity);
        return userMapper.toDTO(userEntity);
    }

}
