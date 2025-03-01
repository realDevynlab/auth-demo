package com.example.authdemo;

import java.util.Optional;
import java.util.UUID;

public interface UserService {

    UserDTO signup(SignupRequest signupRequest);

    Optional<UserEntity> findById(UUID id);

    Optional<UserEntity> findByUsername(String username);

    Optional<UserEntity> findByEmail(String email);

    void createUser(UserEntity user);

    boolean userExists(String username);

}
