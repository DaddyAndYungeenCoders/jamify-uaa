package com.jamify.uaa.domain.mapper;

import com.jamify.uaa.domain.dto.UserDto;
import com.jamify.uaa.domain.model.UserEntity;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

import java.util.List;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface UserMapper {
    UserEntity toEntity(UserDto userDto);

    UserDto toDto(UserEntity userEntity);

    List<UserEntity> toEntity(List<UserDto> userDtos);

    List<UserDto> toDto(List<UserEntity> userEntities);
}
