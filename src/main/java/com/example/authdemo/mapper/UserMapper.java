package com.example.authdemo.mapper;

import com.example.authdemo.dto.UserDTO;
import com.example.authdemo.entity.UserEntity;
import com.example.authdemo.model.SignupRequest;
import org.mapstruct.Mapper;

@Mapper
public interface UserMapper {

	UserEntity toEntity(SignupRequest signupRequest);

	UserDTO toDTO(UserEntity userEntity);

}
