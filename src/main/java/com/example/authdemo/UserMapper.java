package com.example.authdemo;

import org.mapstruct.Mapper;

@Mapper
public interface UserMapper {

	UserEntity toEntity(SignupDTO signupDTO);

	UserDTO toDTO(UserEntity userEntity);

}
