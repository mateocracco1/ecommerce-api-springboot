package com.mateo.springboot.tienda.controller;


import com.mateo.springboot.tienda.dto.user.AdminUserCreateDto;
import com.mateo.springboot.tienda.dto.user.UserDto;
import com.mateo.springboot.tienda.models.Role;
import com.mateo.springboot.tienda.service.user.UserService;
import org.junit.jupiter.api.Test;

import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.mockito.Mockito.when;
import java.util.List;


@WebMvcTest(UserController.class)
@AutoConfigureMockMvc(addFilters = false)
class UserControllerTest {


    @Autowired
    private MockMvc mockMvc; // Simula peticiones HTTP

    @MockitoBean
    private UserService userService; // Simula la capa de servicio

    @Test
    void shouldReturnAllUsers() throws Exception {
        //GETTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTT
        // Simulamos la respuesta del servicio
        List<UserDto> users = List.of(
                new UserDto(1L, "mateo", "mateo@example.com"),
                new UserDto(2L, "lucas", "lucas@example.com"),
                new UserDto(3L, "pep", "pep@example.com")
        );

        when(userService.findAllUsers()).thenReturn(users);

        // Llamamos al endpoint y verificamos la respuesta
        mockMvc.perform(get("/api/users")
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(3))
                .andExpect(jsonPath("$[0].username").value("mateo"))
                .andExpect(jsonPath("$[1].email").value("lucas@example.com"))
                .andExpect(jsonPath("$[2].username").value("pep"))
                .andExpect(jsonPath("$[2].email").value("pep@example.com"));

        Mockito.verify(userService).findAllUsers();

    }

    @Test
    void shouldReturnUserById() throws Exception {
        // Simulamos un usuario
        UserDto user = new UserDto(1L, "mateo", "mateo@example.com");

        when(userService.findUserById(1L)).thenReturn(user);

        mockMvc.perform(get("/api/users/{id}", 1L)
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("mateo"))
                .andExpect(jsonPath("$.email").value("mateo@example.com"));

        Mockito.verify(userService).findUserById(1L);
    }

    @Test
    void shouldCreateAsAdminUser() throws Exception {
        // Simulamos el DTO que recibe el controlador
        AdminUserCreateDto  request = new AdminUserCreateDto(
                "lucas",
                "lucas@example.com",
                "password123"

        );

        // Simulamos la respuesta que devuelve el servicio
        UserDto saved = new UserDto(1L, "lucas", "lucas@example.com");

        when(userService.createUserAsAdmin(Mockito.any(AdminUserCreateDto.class))).thenReturn(saved);

        // JSON que se enviará en la petición POST
        String jsonRequest = """
        {
          "username": "lucas",
          "email": "lucas@example.com",
          "password": "password123",
          "role": "ADMIN"
        }
        """;

        mockMvc.perform(post("/api/users")
                        .contentType(APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("lucas"))
                .andExpect(jsonPath("$.email").value("lucas@example.com"));

        Mockito.verify(userService).createUserAsAdmin(Mockito.any(AdminUserCreateDto.class));
    }



}