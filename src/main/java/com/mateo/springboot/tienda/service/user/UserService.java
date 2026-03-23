package com.mateo.springboot.tienda.service.user;


import com.mateo.springboot.tienda.dto.user.AdminUserCreateDto;

import com.mateo.springboot.tienda.dto.user.UserDto;
import com.mateo.springboot.tienda.dto.user.UserRegisterDto;
import com.mateo.springboot.tienda.dto.user.UserUpdateDto;
import com.mateo.springboot.tienda.models.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface UserService{

    Page<UserDto> findAllUsers(Pageable pageable);    UserDto findUserById(Long id);
    UserDto createUserAsAdmin(AdminUserCreateDto userCreateDto);
    UserDto updateUser(Long id, UserUpdateDto userUpdateDto);
    void deleteUserById(Long id);
    UserDto register(UserRegisterDto dto);
    User findUserOrThrow(Long userId);
}
