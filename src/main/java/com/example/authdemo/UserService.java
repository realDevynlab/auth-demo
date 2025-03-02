package com.example.authdemo;

public interface UserService {

    UserDTO signup(SignupRequest signupRequest);

    UserEntity findByUsername(String username);

    void createUser(UserEntity user);

    boolean userExists(String username);

}
