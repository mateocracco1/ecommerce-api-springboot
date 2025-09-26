package com.mateo.springboot.tienda.service.user;

import com.mateo.springboot.tienda.dto.user.UserCreateDto;
import com.mateo.springboot.tienda.dto.user.UserDto;
import com.mateo.springboot.tienda.dto.user.UserRegisterDto;
import com.mateo.springboot.tienda.dto.user.UserUpdateDto;
import com.mateo.springboot.tienda.mapper.UserMapper;
import com.mateo.springboot.tienda.models.Role;
import com.mateo.springboot.tienda.models.User;
import com.mateo.springboot.tienda.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserServiceImpl implements UserService{


    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public List<UserDto> getUsers() {
        return userRepository.findAll().stream().map(UserMapper::toDto).toList();
    }

    @Override
    public UserDto getUserById(Long id) {
        User user = userRepository.findById(id).orElseThrow(() -> new RuntimeException("User not foud"));
        return UserMapper.toDto(user);
    }

    @Override
    public UserDto createUser(UserCreateDto userCreateDto) {  // admin
        User user = UserMapper.toUser(userCreateDto);
        user.setPassword(passwordEncoder.encode((userCreateDto.getPassword())));
        User save = userRepository.save(user);
        return UserMapper.toDto(save);
    }

    public UserDto register(UserRegisterDto dto) {  //publico
        User user = new User();
        user.setUsername(dto.getUsername());
        user.setEmail(dto.getEmail());
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        user.setRole(Role.CUSTOMER);
        return UserMapper.toDto(userRepository.save(user));
    }

    @Override
    public UserDto updateUser(Long id, UserUpdateDto userUpdateDto) {

        User user = userRepository.findById(id).orElseThrow(() -> new RuntimeException("User not foud"));
        UserMapper.updateUser(user, userUpdateDto);
        if (userUpdateDto.getPassword() != null && !userUpdateDto.getPassword().isBlank()) {
            user.setPassword(passwordEncoder.encode(userUpdateDto.getPassword())); // 🔐 encriptar
        }
        User updated = userRepository.save(user);
        return UserMapper.toDto(updated);
    }

    @Override
    public void deleteUser(Long id) {
        if (!userRepository.existsById(id)){
            throw new RuntimeException("Usuario no encontrado");
        }
        userRepository.deleteById(id);
    }
}
