package com.mateo.springboot.tienda.service.user;

import com.mateo.springboot.tienda.dto.user.UserCreateDto;
import com.mateo.springboot.tienda.dto.user.UserDto;
import com.mateo.springboot.tienda.dto.user.UserRegisterDto;
import com.mateo.springboot.tienda.dto.user.UserUpdateDto;
import com.mateo.springboot.tienda.exceptions.user.EmailAlreadyExistsException;
import com.mateo.springboot.tienda.exceptions.user.InvalidUserIdException;
import com.mateo.springboot.tienda.exceptions.user.UserNotFoundException;
import com.mateo.springboot.tienda.exceptions.user.UsernameAlreadyExistsException;
import com.mateo.springboot.tienda.mapper.UserMapper;
import com.mateo.springboot.tienda.models.Role;
import com.mateo.springboot.tienda.models.User;
import com.mateo.springboot.tienda.repository.UserRepository;
import jakarta.transaction.Transactional;
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
    public List<UserDto> findAllUsers() {
        return userRepository.findAll().stream().map(UserMapper::toDto).toList();
    }

    @Override
    public UserDto findUserById(Long id) {
        User user = findUserOrThrow(id);
        return UserMapper.toDto(user);
    }

    @Override
    public UserDto createUserAsAdmin(UserCreateDto userCreateDto) {  // admin

        if (userRepository.existsByEmail(userCreateDto.getEmail())) {
            throw new EmailAlreadyExistsException(userCreateDto.getEmail());
        }

        if (userRepository.existsByUsername(userCreateDto.getUsername())) {
            throw new UsernameAlreadyExistsException(userCreateDto.getUsername());
        }

        User user = UserMapper.toUser(userCreateDto);
        user.setPassword(passwordEncoder.encode((userCreateDto.getPassword())));
        User save = userRepository.save(user);
        return UserMapper.toDto(save);
    }

    @Override
    public UserDto registerUser(UserRegisterDto dto) {

        if (userRepository.existsByEmail(dto.getEmail())) {
            throw new EmailAlreadyExistsException(dto.getEmail());
        }

        if (userRepository.existsByUsername(dto.getUsername())) {
            throw new UsernameAlreadyExistsException(dto.getUsername());
        }

        User user = new User();
        user.setUsername(dto.getUsername());
        user.setEmail(dto.getEmail());
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        user.setRole(Role.CUSTOMER);
        return UserMapper.toDto(userRepository.save(user));
    }

    @Override
    public UserDto updateUser(Long id, UserUpdateDto userUpdateDto) {

        User user = findUserOrThrow(id);
        UserMapper.updateUser(user, userUpdateDto);
        if (userUpdateDto.getPassword() != null && !userUpdateDto.getPassword().isBlank()) {
            user.setPassword(passwordEncoder.encode(userUpdateDto.getPassword()));
        }
        User updated = userRepository.save(user);
        return UserMapper.toDto(updated);
    }


    @Transactional
    @Override
    public void deleteUserById(Long id) {
        User user = findUserOrThrow(id);
        userRepository.delete(user);
    }

    @Override
    public User findUserOrThrow(Long userId) {
        if (userId == null || userId <= 0) {
            throw new InvalidUserIdException();
        }
        return userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));
    }
}
