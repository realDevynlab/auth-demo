package com.example.authdemo.mapper;

import com.example.authdemo.dto.UserDTO;
import com.example.authdemo.entity.UserEntity;
import org.mapstruct.Mapper;

@Mapper
public interface AuthenticationMapper {

    UserDTO toDTO(UserEntity userEntity);

}
