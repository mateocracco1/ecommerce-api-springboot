package com.mateo.springboot.tienda.service.user;


import com.mateo.springboot.tienda.dto.user.UserCreateDto;
import com.mateo.springboot.tienda.dto.user.UserDto;
import com.mateo.springboot.tienda.dto.user.UserUpdateDto;

import java.util.List;

public interface UserService{

    List<UserDto>getUsers();
    UserDto getUserById(Long id);
    UserDto createUser(UserCreateDto userCreateDto);
    UserDto updateUser(Long id, UserUpdateDto userUpdateDto);// actualiza un usuario
    void deleteUser(Long id);


}
