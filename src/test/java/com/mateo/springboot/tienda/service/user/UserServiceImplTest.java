package com.mateo.springboot.tienda.service.user;

import com.mateo.springboot.tienda.dto.user.AdminUserCreateDto;
import com.mateo.springboot.tienda.dto.user.UserDto;
import com.mateo.springboot.tienda.dto.user.UserRegisterDto;
import com.mateo.springboot.tienda.dto.user.UserUpdateDto;
import com.mateo.springboot.tienda.exceptions.user.UserNotFoundException;
import com.mateo.springboot.tienda.models.Role;
import com.mateo.springboot.tienda.models.User;
import com.mateo.springboot.tienda.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @InjectMocks
    private UserServiceImpl userService; // Servicio real, con mocks inyectados

    @Mock
    private UserRepository userRepository; // Mock del repositorio

    @Mock
    private PasswordEncoder passwordEncoder;

    //------------------------------------------------------------------------------

    @Test
    void testFindAllUsers_emptyList() {
        when(userRepository.findAll()).thenReturn(Collections.emptyList());

        List<UserDto> result = userService.findAllUsers();

        assertThat(result).isEmpty();
    }

    @Test
    void testCreateUser_success() {
        AdminUserCreateDto dto = new AdminUserCreateDto ("lucas", "lucas@example.com", "password123");

        User savedUser = new User();
        savedUser.setId(1L);
        savedUser.setUsername("lucas");
        savedUser.setEmail("lucas@example.com");
        savedUser.setRole(Role.ADMIN);

        when(userRepository.save(any(User.class))).thenReturn(savedUser);

        UserDto result = userService.createUserAsAdmin(dto);

        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getUsername()).isEqualTo("lucas");
    }


    @Test
    void testFindById_userNotFound() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(UserNotFoundException.class, () -> userService.findUserById(99L));
    }

    @Test
    void testUpdateUser_withPassword_success() {

        Long id = 1L;
        User existing = new User();
        existing.setId(id);
        existing.setUsername("lucas");
        existing.setPassword("oldPass");

        UserUpdateDto dto = new UserUpdateDto();
        dto.setUsername("lucas_updated");
        dto.setPassword("newPass");

        // mocks
        when(userRepository.findById(id)).thenReturn(Optional.of(existing));
        when(passwordEncoder.encode("newPass")).thenReturn("encodedPass");
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // ejecutar
        UserDto result = userService.updateUser(id, dto);

        // verificar
        assertThat(result.getUsername()).isEqualTo("lucas_updated");
        verify(passwordEncoder).encode("newPass");
        verify(userRepository).save(existing);

    }
    @Test
    void testDeleteUserById(){
        Long id = 1L;
        User user = new User();
        user.setId(id);

        when(userRepository.findById(id)).thenReturn(Optional.of(user));

        userService.deleteUserById(id);

        // Verifica que se llamó a delete con el usuario correcto
        verify(userRepository).delete(user);

    }

    @Test
    void testDeleteUserByIdNotFound(){

        // ID del usuario que no existe
        Long id = 1L;
        
        // Simula que no se encuentra el usuario en el repositorio
        when(userRepository.findById(id)).thenReturn(Optional.empty());

        // Verifica que se lanza la excepción UserNotFoundException
        assertThrows(UserNotFoundException.class, () -> userService.deleteUserById(id));

        // Verifica que *nunca* se llamó al método delete()
        verify(userRepository, never()).delete(any());
    }

    @Test
    void testRegisterUser_Success() {
        // 📋 Arrange (preparar)
        UserRegisterDto dto = new UserRegisterDto();
        dto.setUsername("mateo");
        dto.setEmail("mateo@example.com");
        dto.setPassword("1234");


        // Simulamos el encode
        when(passwordEncoder.encode("1234")).thenReturn("encoded1234");

        // Creamos el User que se devolvería del repo
        User savedUser = new User();
        savedUser.setId(1L);
        savedUser.setUsername("mateo");
        savedUser.setEmail("mateo@example.com");
        savedUser.setPassword("encoded1234");
        savedUser.setRole(Role.CUSTOMER);
        // Mockeamos el save
        when(userRepository.save(any(User.class))).thenReturn(savedUser);

        // 🧭 Act (acción)
        UserDto result = userService.register(dto);

        // ✅ Assert (verificación)
        assertNotNull(result);
        assertEquals("mateo", result.getUsername());
        assertEquals("mateo@example.com", result.getEmail());

        // Verifica que la contraseña se haya codificado
        verify(passwordEncoder).encode("1234");

        // Verifica que se haya guardado el user con los valores correctos
        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(captor.capture());
        User capturedUser = captor.getValue();

        assertEquals("mateo", capturedUser.getUsername());
        assertEquals("mateo@example.com", capturedUser.getEmail());
        assertEquals("encoded1234", capturedUser.getPassword());
        assertEquals(Role.CUSTOMER, capturedUser.getRole());
    }


}




