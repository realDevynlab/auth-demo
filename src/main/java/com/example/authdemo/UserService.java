package com.example.authdemo;

public interface UserService {

    void createUser(UserEntity user);

    boolean userExists(String username);

    UserDTO signup(SignupDTO signupDTO);

}
