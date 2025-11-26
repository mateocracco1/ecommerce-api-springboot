package com.mateo.springboot.tienda.dto.user;


import jakarta.validation.constraints.Size;

public class UserUpdateDto {

    @Size(min = 3, max = 50, message = "Username must be between 3 and 50 characters")
    private String username;

    @Size(min = 6, message = "Password must have at least 6 characters")
    private  String password;

    //agregar email para modif

    public UserUpdateDto() {
    }

    public UserUpdateDto(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
