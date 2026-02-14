package com.mateo.springboot.tienda.service.user;


import com.mateo.springboot.tienda.dto.user.AdminUserCreateDto;
import com.mateo.springboot.tienda.mapper.UserMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import com.mateo.springboot.tienda.dto.user.UserDto;
import com.mateo.springboot.tienda.dto.user.UserRegisterDto;
import com.mateo.springboot.tienda.dto.user.UserUpdateDto;
import com.mateo.springboot.tienda.exceptions.user.EmailAlreadyExistsException;
import com.mateo.springboot.tienda.exceptions.user.InvalidUserIdException;
import com.mateo.springboot.tienda.exceptions.user.UserNotFoundException;
import com.mateo.springboot.tienda.exceptions.user.UsernameAlreadyExistsException;
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
    private final   Logger log  = LoggerFactory.getLogger(UserServiceImpl.class);
    private final UserMapper userMapper;

    public UserServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder, UserMapper userMapper) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.userMapper = userMapper;
    }

    @Override
    public List<User> findAllUsers() {
        return userRepository.findAll();
    }

    @Override
    public User findUserById(Long id) {
        User user = findUserOrThrow(id);
        return user;
    }

    @Override
    public User createUserAsAdmin(AdminUserCreateDto userCreateDto) {  // admin

        log.info("Attempting to create user as ADMIN with email: {}", userCreateDto.getEmail());


        if (userRepository.existsByEmail(userCreateDto.getEmail())) {
            log.warn("Email already exists: {}", userCreateDto.getEmail());
            throw new EmailAlreadyExistsException(userCreateDto.getEmail());
        }

        if (userRepository.existsByUsername(userCreateDto.getUsername())) {
            log.warn("Username already exists: {}", userCreateDto.getUsername());
            throw new UsernameAlreadyExistsException(userCreateDto.getUsername());
        }

        User user = userMapper.toEntity(userCreateDto);
        user.setRole(Role.ADMIN);
        user.setPassword(passwordEncoder.encode((userCreateDto.getPassword())));
        User save = userRepository.save(user);
        log.info("Admin created new user successfully with id {}", save.getId());
        return save;
    }

    @Override
    public User register(UserRegisterDto dto) {   //register publico
        log.info("Trying to register a user with email: {}", dto.getEmail());

        if (userRepository.existsByEmail(dto.getEmail())) {
            log.warn("Email already exists: {}", dto.getEmail());
            throw new EmailAlreadyExistsException(dto.getEmail());
        }

        if (userRepository.existsByUsername(dto.getUsername())) {
            log.warn("Username already exists: {}", dto.getUsername());
            throw new UsernameAlreadyExistsException(dto.getUsername());
        }

        User user = new User();
        user.setUsername(dto.getUsername());
        user.setEmail(dto.getEmail());
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        user.setRole(Role.CUSTOMER);
        user.setEnabled(true);

        User saved = userRepository.save(user);
        log.info("User registered successfully with id {}", saved.getId());
        return saved;
    }

    @Override
    public User updateUser(Long id, UserUpdateDto userUpdateDto) {

        log.info("Attempting to update user with id: {}", id);

        User user = findUserOrThrow(id);
        userMapper.updateUser(user, userUpdateDto);

        if (userUpdateDto.getPassword() != null && !userUpdateDto.getPassword().isBlank()) {
            log.debug("Updating password for user id {}", id);
            user.setPassword(passwordEncoder.encode(userUpdateDto.getPassword()));
        }
        User updated = userRepository.save(user);
        log.info("User updated successfully with id: {}", updated.getId());
        return updated;
    }


    @Transactional
    @Override
    public void deleteUserById(Long id) {

        log.info("Attempting to delete user with id: {}", id);

        User user = findUserOrThrow(id);

        userRepository.delete(user);
        log.info("User deleted successfully with id: {}", id);
    }

    @Override
    public User findUserOrThrow(Long userId) {
        if (userId == null || userId <= 0) {
            log.warn("Invalid userId received: {}", userId);
            throw new InvalidUserIdException();
        }
        log.debug("Searching user with id {}", userId);
        return userRepository.findById(userId)
                .orElseThrow(() -> {
                    log.warn("User not found with id {}", userId);
                    return new UserNotFoundException(userId);
                });
    }
}
