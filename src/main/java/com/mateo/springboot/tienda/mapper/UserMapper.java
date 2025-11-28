package com.mateo.springboot.tienda.mapper;

import com.mateo.springboot.tienda.dto.user.UserCreateDto;
import com.mateo.springboot.tienda.dto.user.UserDto;
import com.mateo.springboot.tienda.dto.user.UserUpdateDto;
import com.mateo.springboot.tienda.models.User;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {


        public static User toUser(UserDto userDto){
            User user = new User();
            user.setId(userDto.getId());
            user.setUsername(userDto.getUsername());
            user.setEmail(userDto.getEmail());
            return user;
        }


        public static UserDto toDto(User user){
            UserDto userDto = new UserDto();
            userDto.setId(user.getId());
            userDto.setUsername(user.getUsername());
            userDto.setEmail(user.getEmail());
            return userDto;
        }

        //Create

        public  static User toUser(UserCreateDto dto){
            User user = new User();
            user.setUsername(dto.getUsername());
            user.setEmail(dto.getEmail());
            user.setPassword(dto.getPassword()); // 🔐 importante
            user.setRole(dto.getRole());
            return user;
        }

        //Update
        public static void updateUser(User user, UserUpdateDto dto) {
            if (dto.getUsername() != null && !dto.getUsername().isBlank()) {
                user.setUsername(dto.getUsername());
            }

        }

}
