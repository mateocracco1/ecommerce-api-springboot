package com.mateo.springboot.tienda.service.user;

import com.mateo.springboot.tienda.dto.user.AdminUserCreateDto;
import com.mateo.springboot.tienda.dto.user.UserRegisterDto;
import com.mateo.springboot.tienda.dto.user.UserUpdateDto;
import com.mateo.springboot.tienda.exceptions.user.UserNotFoundException;
import com.mateo.springboot.tienda.mapper.UserMapper;
import com.mateo.springboot.tienda.models.Role;
import com.mateo.springboot.tienda.models.User;
import com.mateo.springboot.tienda.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {


    @Mock
    private UserRepository userRepository; // Simulamos la base de datos

    @InjectMocks
    private UserServiceImpl userService; // El servicio real que estamos probando

    @Mock
    private UserMapper userMapper;

    @Mock
    private PasswordEncoder passwordEncoder;


    @Test
    void findAllUsers() {

        List<User> mockUsers = Arrays.asList(new User(), new User(), new User());
        when(userRepository.findAll()).thenReturn(mockUsers);

        List<User> result = userService.findAllUsers();


        // 4. Los asserts y el verify (Then / Assert)
        assertNotNull(result); // Verificamos que no devuelva nulo
        assertEquals(2, result.size()); // Verificamos que traiga los 2 usuarios

        // Verificamos que el servicio efectivamente se comunicó con el repositorio
        verify(userRepository).findAll();

    }

    @Test
    void findUserById() {


        // 1. Arrange / Given
        User user = new User();
        user.setId(1L); // Es buena práctica setear el dato que vas a evaluar
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        // 2. Act / When
        User result = userService.findUserById(1L);

        // 3. Assert / Then
        assertNotNull(result); // Comprobamos que el servicio no devuelva null
        assertEquals(1L, result.getId()); // Comprobamos que traiga al usuario correcto

        verify(userRepository).findById(1L); // Tu verify, que está genial
    }

    @Test
    void findUserById_WhenUserDoesNotExist_ThrowsException() {
        // 1. Arrange / Given
        Long userId = 99L; // Usamos un ID que asumimos que no existe

        // Le decimos al mock que devuelva un Optional vacío,
        // simulando que la base de datos no encontró nada
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // 2 y 3. Act & Assert / When & Then
        // assertThrows recibe el tipo de excepción esperada y una función lambda '() ->' con el código que debería fallar
        assertThrows(UserNotFoundException.class, () -> {
            userService.findUserById(userId);
        });

        // 4. Verify
        // Comprobamos que el servicio efectivamente intentó buscar en la base de datos antes de rendirse
        verify(userRepository).findById(userId);
    }


    @Test
    void createUserAsAdmin() {

        // 1. Preparamos el DTO de entrada
        AdminUserCreateDto dto = new AdminUserCreateDto();
        dto.setEmail("admin@test.com");
        dto.setUsername("admin123");
        dto.setPassword("123456");

        // 2. Preparamos la entidad "cruda" que devolverá el mapper
        User mappedUser = new User();
        // No le seteamos el ID porque todavía no se guardó

        // 3. Preparamos la entidad final que simula devolver la base de datos
        User savedUser = new User();
        savedUser.setId(1L);
        savedUser.setRole(Role.ADMIN);
        savedUser.setPassword("contraseña_encriptada");

        // 4. Entrenamos a los Mocks para que respondan lo que necesitamos
        // Decimos que el email y el usuario NO existen para pasar las validaciones
        when(userRepository.existsByEmail("admin@test.com")).thenReturn(false);
        when(userRepository.existsByUsername("admin123")).thenReturn(false);

        // Simulamos la conversión del mapper
        when(userMapper.toEntity(dto)).thenReturn(mappedUser);

        // Simulamos la encriptación de la contraseña
        when(passwordEncoder.encode("123456")).thenReturn("contraseña_encriptada");

        // Simulamos el guardado en la base de datos
        when(userRepository.save(mappedUser)).thenReturn(savedUser);


        // --- WHEN (Ejecución) ---
        User result = userService.createUserAsAdmin(dto);


        // --- THEN (Verificación) ---

        // Comprobamos los resultados
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals(Role.ADMIN, result.getRole());
        assertEquals("contraseña_encriptada", result.getPassword());

        // Verificamos que se llamó a cada dependencia como esperábamos
        verify(userRepository).existsByEmail("admin@test.com");
        verify(userRepository).existsByUsername("admin123");
        verify(userMapper).toEntity(dto);
        verify(passwordEncoder).encode("123456");
        verify(userRepository).save(mappedUser);

    }

    @Test
    void register() {

        UserRegisterDto dto = new UserRegisterDto();
        dto.setEmail("user@test.com");
        dto.setUsername("user123");
        dto.setPassword("123456");

        User setUser = new User();



        User savedUser = new User();
        savedUser.setId(1L);
        savedUser.setRole(Role.CUSTOMER);
        savedUser.setPassword("contraseña_encriptada");


        when(userRepository.existsByEmail("user@test.com")).thenReturn(false);
        when(userRepository.existsByUsername("user123")).thenReturn(false);

        when(passwordEncoder.encode("123456")).thenReturn("contraseña_encriptada");


        when(userRepository.save(any(User.class))).thenReturn(savedUser);

        User result = userService.register(dto);


        assertNotNull(savedUser);
        assertEquals(1L, result.getId());
        assertEquals(Role.CUSTOMER, result.getRole());

        verify(userRepository).existsByEmail("user@test.com");
        verify(userRepository).existsByUsername("user123");
        verify(passwordEncoder).encode("123456");
        verify(userRepository).save(any(User.class));

    }

    @Test
    void updateUser() {

        Long userId = 1L;

        UserUpdateDto dto = new UserUpdateDto();
        dto.setPassword("nuevo_password");

        User existingUser = new User();
        existingUser.setId(userId);
        existingUser.setPassword("password_viejo");

        User savedUser = new User();
        savedUser.setId(userId);
        savedUser.setUsername("nuevo_usuario");
        savedUser.setPassword("password_encriptada");

        when(userRepository.findById(userId)).thenReturn(Optional.of(existingUser));

        doNothing().when(userMapper).updateUser(existingUser, dto);

        when(passwordEncoder.encode("nuevo_password")).thenReturn("password_encriptada");

        when(userRepository.save(existingUser)).thenReturn(savedUser);


        User result = userService.updateUser(userId, dto);


        assertNotNull(result);
        assertEquals(userId, result.getId());
        assertEquals("password_encriptada", result.getPassword());

        verify(userRepository).findById(userId);
        verify(userMapper).updateUser(existingUser, dto);
        verify(passwordEncoder).encode("nuevo_password");
        verify(userRepository).save(existingUser);
    }

    @Test
    void deleteUserById() {

        Long userId = 1L;

        User user = new User();
        user.setId(userId);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        doNothing().when(userRepository).delete(user);

        userService.deleteUserById(userId);

        verify(userRepository).findById(userId);

        // Verificamos que efectivamente se mandó a eliminar a ESE usuario
        verify(userRepository).delete(user);



    }

    @Test
    void findUserOrThrow() {

        Long userId = 1L;

        User user = new User();
        user.setId(userId);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        User result = userService.findUserOrThrow(userId);

        assertNotNull(result);

        verify(userRepository).findById(userId);


    }

    @Test
    void findUserOrThrow_WhenIdIsInvalid_ThrowsException() {

        Long invalidUserId = 0L;

        // Act & Assert
        assertThrows(UserNotFoundException.class, () -> {
            userService.findUserOrThrow(invalidUserId);
        });


        verify(userRepository, never()).findById(anyLong());
    }
}