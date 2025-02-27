package com.example.authdemo;

import org.mapstruct.Mapper;

@Mapper
public interface AuthenticationMapper {

    UserEntity toEntity(SignupDTO signupDTO);

    UserDTO toDTO(UserEntity userEntity);

}
