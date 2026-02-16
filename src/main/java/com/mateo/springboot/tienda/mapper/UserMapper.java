package com.mateo.springboot.tienda.mapper;


import com.mateo.springboot.tienda.dto.user.AdminUserCreateDto;
import com.mateo.springboot.tienda.dto.user.UserDto;
import com.mateo.springboot.tienda.dto.user.UserRegisterDto;
import com.mateo.springboot.tienda.dto.user.UserUpdateDto;
import com.mateo.springboot.tienda.models.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.springframework.stereotype.Component;


@Component
@Mapper(componentModel = "spring")
public interface UserMapper {



    UserDto toDto(User user);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "role", ignore = true)
    User toEntity(AdminUserCreateDto dto);


    @Mapping(target = "id", ignore = true)
    @Mapping(target = "role", ignore = true)
    @Mapping(target = "enabled", constant = "true")
    User toEntity(UserRegisterDto dto);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "password", ignore = true)
    void updateUser(@MappingTarget User user, UserUpdateDto dto);
}




