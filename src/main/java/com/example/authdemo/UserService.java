package com.example.authdemo;

import java.util.Optional;
import java.util.UUID;

public interface UserService {

    UserDTO signup(SignupDTO signupDTO);

    Optional<UserEntity> findById(UUID id);

    void createUser(UserEntity user);

    boolean userExists(String username);

}
