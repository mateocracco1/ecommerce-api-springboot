package com.mateo.springboot.tienda.dto.auth;

public class LoginResponseDto {

    private String token;
    private String tokenType = "Bearer";
    private String email;

    public LoginResponseDto(String token, String email) {
        this.token = token;
        this.email = email;
    }

    public String getTokenType() {
        return tokenType;
    }


    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
