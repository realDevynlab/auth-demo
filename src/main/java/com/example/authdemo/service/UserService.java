package com.example.authdemo.service;

import com.example.authdemo.dto.UserDTO;
import com.example.authdemo.entity.UserEntity;
import com.example.authdemo.model.SignupRequest;

public interface UserService {

    UserDTO signup(SignupRequest signupRequest);

    UserEntity findByUsername(String username);

    void createUser(UserEntity user);

    boolean userExists(String username);

}
