package com.example.authdemo;

import org.mapstruct.Mapper;

@Mapper
public interface UserMapper {

	UserEntity toEntity(SignupRequest signupRequest);

	UserDTO toDTO(UserEntity userEntity);

}
