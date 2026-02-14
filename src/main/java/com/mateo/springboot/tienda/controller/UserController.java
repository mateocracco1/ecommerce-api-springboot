package com.mateo.springboot.tienda.controller;

import com.mateo.springboot.tienda.dto.user.AdminUserCreateDto;
import com.mateo.springboot.tienda.dto.user.UserDto;
import com.mateo.springboot.tienda.dto.user.UserUpdateDto;
import com.mateo.springboot.tienda.mapper.UserMapper;
import com.mateo.springboot.tienda.security.CustomUserDetails;
import com.mateo.springboot.tienda.service.user.UserService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;
    private final Logger log  = LoggerFactory.getLogger(UserController.class);
    private final UserMapper userMapper;


    public UserController(UserService userService, UserMapper userMapper) {
        this.userService = userService;
        this.userMapper = userMapper;
    }


    //agregar para ver mis propios datos
    //public User getMyInfo(@AuthenticationPrincipal Object principal)

    @GetMapping("/me")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<UserDto> getMyInfo(
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        UserDto userDto = userMapper.toDto(userService.findUserById(userDetails.getId()));
        return ResponseEntity.ok(userDto);
    }


    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<UserDto>> getUsers(){
        return ResponseEntity.ok(userService.findAllUsers().stream().map(userMapper::toDto).toList());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or #id == authentication.principal.id")
    public  ResponseEntity<UserDto> geUsertById(@PathVariable Long id){
        log.info("GET /api/users/{} - Fetching user", id);
        UserDto userDto = userMapper.toDto(userService.findUserById(id));
        return ResponseEntity.ok(userDto);
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserDto>createUserAsAdmin(@Valid  @RequestBody AdminUserCreateDto userCreateDto){
        log.info("POST /api/users/{} - Creating user as admin", userCreateDto.getUsername());
        UserDto user = userMapper.toDto(userService.createUserAsAdmin(userCreateDto));
        return ResponseEntity.status(HttpStatus.CREATED).body(user);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or #id == authentication.principal.id")
    public ResponseEntity<UserDto>updateUser(@PathVariable Long id,@Valid @RequestBody UserUpdateDto updateDto){
        log.info("PUT /api/users/{} - Updating user", id);
        UserDto user = userMapper.toDto(userService.updateUser(id,updateDto));
        return  ResponseEntity.ok(user);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        log.info("DELETE /api/users/{} - Deleting user", id);
        userService.deleteUserById(id);
        return ResponseEntity.noContent().build();
    }
}
