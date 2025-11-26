package com.mateo.springboot.tienda.controller;

import com.mateo.springboot.tienda.dto.product.ProductDto;
import com.mateo.springboot.tienda.dto.user.UserCreateDto;
import com.mateo.springboot.tienda.dto.user.UserDto;
import com.mateo.springboot.tienda.dto.user.UserUpdateDto;
import com.mateo.springboot.tienda.service.order.OrderServiceImpl;
import com.mateo.springboot.tienda.service.user.UserService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;
    private final Logger log  = LoggerFactory.getLogger(UserController.class);


    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public ResponseEntity<List<UserDto>>getAllUsers(){
        return ResponseEntity.ok(userService.findAllUsers());
    }

    @GetMapping("/{id}")
    public  ResponseEntity<UserDto>getUserById(@PathVariable Long id){
        log.info("GET /api/users/{} - Fetching user", id);
        return ResponseEntity.ok(userService.findUserById(id));
    }

    @PostMapping
    public ResponseEntity<UserDto>createUserAsAdmin(@Valid  @RequestBody  UserCreateDto userCreateDto){
        log.info("POST /api/users/{} - Creating user as admin", userCreateDto.getUsername());
        UserDto user = userService.createUserAsAdmin(userCreateDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(user);
    }
    @PutMapping("/{id}")
    public ResponseEntity<UserDto>updateUser(@PathVariable Long id,@Valid @RequestBody UserUpdateDto updateDto){
        log.info("PUT /api/users/{} - Updating user", updateDto.getUsername());
        UserDto user = userService.updateUser(id,updateDto);
        return  ResponseEntity.ok(user);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        log.info("DELETE /api/users/{} - Deleting user", id);
        userService.deleteUserById(id);
        return ResponseEntity.noContent().build();
    }
}
