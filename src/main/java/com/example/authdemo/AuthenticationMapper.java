package com.example.authdemo;

import org.mapstruct.Mapper;

@Mapper
public interface AuthenticationMapper {

    UserDTO toDTO(UserEntity userEntity);

}
