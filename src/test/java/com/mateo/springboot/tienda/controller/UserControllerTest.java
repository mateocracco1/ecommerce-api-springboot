package com.mateo.springboot.tienda.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mateo.springboot.tienda.dto.user.UserDto;
import com.mateo.springboot.tienda.dto.user.UserUpdateDto;
import com.mateo.springboot.tienda.mapper.UserMapper;
import com.mateo.springboot.tienda.models.Role;
import com.mateo.springboot.tienda.models.User;
import com.mateo.springboot.tienda.security.CustomUserDetails;
import com.mateo.springboot.tienda.security.CustomUserDetailsService;
import com.mateo.springboot.tienda.security.JwtUtil;
import com.mateo.springboot.tienda.service.user.UserServiceImpl;
import org.apache.catalina.LifecycleState;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.MediaType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;

import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.security.test.context.support.WithMockUser;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;


import static org.junit.jupiter.api.Assertions.*;

@WebMvcTest(UserController.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserServiceImpl userService;

    @MockitoBean
    private UserMapper userMapper;

    @MockitoBean
    private JwtUtil jwtUtil;


    @MockitoBean
    private CustomUserDetailsService customUserDetailsService;

    @Autowired
    private ObjectMapper objectMapper; // Herramienta para convertir Java a JSON

    @Test
    @WithMockUser(roles = "ADMIN")
    void getUserById_Returns200AndUser() throws Exception{

        Long userId = 1L;

        User mockUser = new User();
        mockUser.setId(userId);
        mockUser.setUsername("usuario_prueba");

        UserDto mockDto = new UserDto();
        mockDto.setId(userId);
        mockDto.setUsername("usuario_prueba");

        when(userService.findUserById(userId)).thenReturn(mockUser);

        when(userMapper.toDto(mockUser)).thenReturn(mockDto);

        mockMvc.perform(get("/api/users/{id}", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(userId))
                .andExpect(jsonPath("$.username").value("usuario_prueba"));


        verify(userService).findUserById(userId);
        verify(userMapper).toDto(mockUser);

    }


    @Test
    @WithMockUser(roles = "ADMIN")
    void updateUser_Returns200AndUser() throws Exception{
        Long userId = 1L;

        UserUpdateDto userUpdateDto = new UserUpdateDto();
        userUpdateDto.setUsername("new_username");


        User mockUser = new User();
        mockUser.setId(userId);
        mockUser.setUsername("new_username");

        UserDto mockDto = new UserDto();
        mockDto.setId(userId);
        mockDto.setUsername("new_username");

        when(userService.updateUser(eq(userId), any(UserUpdateDto.class))).thenReturn(mockUser);
        when(userMapper.toDto(mockUser)).thenReturn(mockDto);

        mockMvc.perform(put("/api/users/{id}", userId)
                        .with(csrf())
                        .contentType(String.valueOf(MediaType.APPLICATION_JSON))
                        .content(objectMapper.writeValueAsString(userUpdateDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("new_username"));

    }



    @Test
    @WithMockUser(roles = "ADMIN")
    void getUsers_Returns200AndUsers() throws Exception{

        User user1 = new User(); user1.setId(1L);
        User user2 = new User(); user2.setId(2L);
        List<User> userList = Arrays.asList(user1, user2);

        UserDto dto1 = new UserDto(); dto1.setId(1L);
        UserDto dto2 = new UserDto(); dto2.setId(2L);

        when(userService.findAllUsers()).thenReturn(userList);
        when(userMapper.toDto(user1)).thenReturn(dto1);
        when(userMapper.toDto(user2)).thenReturn(dto2);


        mockMvc.perform(get("/api/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[1].id").value(2));

        verify(userService).findAllUsers();

        verify(userMapper, times(2)).toDto(any(User.class));
    }



    @Test
    void getMyInfo()throws Exception{

        Long myId = 10L;

        // A. Preparamos la entidad base
        User realUser = new User();
        realUser.setId(myId);
        realUser.setUsername("mateo_dev");
        realUser.setPassword("123456");
        realUser.setRole(Role.CUSTOMER);
        CustomUserDetails myUserDetails = new CustomUserDetails(realUser);

        UserDto mockDto = new UserDto();
        mockDto.setId(myId);
        mockDto.setUsername("mateo_dev");

        when(userService.findUserById(myId)).thenReturn(realUser);
        when(userMapper.toDto(realUser)).thenReturn(mockDto);

        mockMvc.perform(get("/api/users/me") // Ajusta la URL si tu controlador tiene otra ruta base
                        .with(user(myUserDetails)))  // <-- ¡LA MAGIA AQUÍ! Inyectamos el usuario personalizado
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(myId))
                .andExpect(jsonPath("$.username").value("mateo_dev"));

        // --- 4. VERIFY ---
        verify(userService).findUserById(myId);
        verify(userMapper).toDto(realUser);

    }



}