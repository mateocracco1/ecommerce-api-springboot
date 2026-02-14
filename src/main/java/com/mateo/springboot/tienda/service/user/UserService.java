package com.mateo.springboot.tienda.service.user;


import com.mateo.springboot.tienda.dto.user.AdminUserCreateDto;

import com.mateo.springboot.tienda.dto.user.UserDto;
import com.mateo.springboot.tienda.dto.user.UserRegisterDto;
import com.mateo.springboot.tienda.dto.user.UserUpdateDto;
import com.mateo.springboot.tienda.models.Product;
import com.mateo.springboot.tienda.models.User;

import java.util.List;

public interface UserService{

    List<User>findAllUsers();
    User findUserById(Long id);
    User createUserAsAdmin(AdminUserCreateDto userCreateDto);
    User updateUser(Long id, UserUpdateDto userUpdateDto);// actualiza un usuario
    void deleteUserById(Long id);
    User register(UserRegisterDto dto);
    User findUserOrThrow(Long userId);



}
