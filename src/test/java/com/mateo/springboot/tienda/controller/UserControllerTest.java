package com.mateo.springboot.tienda.controller;

import com.mateo.springboot.tienda.dto.user.UserDto;
import com.mateo.springboot.tienda.service.user.UserService;
import org.junit.jupiter.api.Test;

import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.mockito.Mockito.when;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;


@WebMvcTest(UserController.class)
@AutoConfigureMockMvc(addFilters = false)
class UserControllerTest {


    @Autowired
    private MockMvc mockMvc; // Simula peticiones HTTP

    @MockitoBean
    private UserService userService; // Simula la capa de servicio

    @Test
    void shouldReturnAllUsers() throws Exception {

        // Simulamos la respuesta del servicio
        List<UserDto> users = List.of(
                new UserDto(1L, "mateo", "mateo@example.com"),
                new UserDto(2L, "lucas", "lucas@example.com")
        );

        when(userService.findAllUsers()).thenReturn(users);

        // Llamamos al endpoint y verificamos la respuesta
        mockMvc.perform(get("/api/users")
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].name").value("mateo"))
                .andExpect(jsonPath("$[1].email").value("lucas@example.com"));

        Mockito.verify(userService).findAllUsers();

    }
}