package com.mateo.springboot.tienda.service.user;


import com.mateo.springboot.tienda.dto.user.AdminUserCreateDto;

import com.mateo.springboot.tienda.dto.user.UserDto;
import com.mateo.springboot.tienda.dto.user.UserRegisterDto;
import com.mateo.springboot.tienda.dto.user.UserUpdateDto;
import com.mateo.springboot.tienda.models.Product;
import com.mateo.springboot.tienda.models.User;

import java.util.List;

public interface UserService{

    List<UserDto>findAllUsers();
    UserDto findUserById(Long id);
    UserDto createUserAsAdmin(AdminUserCreateDto userCreateDto);
    UserDto updateUser(Long id, UserUpdateDto userUpdateDto);// actualiza un usuario
    void deleteUserById(Long id);
    UserDto registerUser(UserRegisterDto dto);
    User findUserOrThrow(Long userId);

}
