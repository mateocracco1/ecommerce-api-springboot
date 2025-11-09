package com.mateo.springboot.tienda.controller;

import com.mateo.springboot.tienda.dto.product.ProductDto;
import com.mateo.springboot.tienda.dto.user.UserCreateDto;
import com.mateo.springboot.tienda.dto.user.UserDto;
import com.mateo.springboot.tienda.dto.user.UserUpdateDto;
import com.mateo.springboot.tienda.service.user.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public ResponseEntity<List<UserDto>>getAllUsers(){
        return ResponseEntity.ok(userService.findAllUsers());
    }

    @GetMapping("/{id}")
    public  ResponseEntity<UserDto>getUserById(@PathVariable Long id){
        return ResponseEntity.ok(userService.findUserById(id));
    }

    @PostMapping
    public ResponseEntity<UserDto>createUserAsAdmin(@Valid  @RequestBody  UserCreateDto userCreateDto){
        UserDto user = userService.createUserAsAdmin(userCreateDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(user);
    }
    @PutMapping("/{id}")
    public ResponseEntity<UserDto>updateUser(@PathVariable Long id,@Valid @RequestBody UserUpdateDto updateDto){
        UserDto user = userService.updateUser(id,updateDto);
        return  ResponseEntity.ok(user);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userService.deleteUserById(id);
        return ResponseEntity.noContent().build();
    }
}
