package com.mateo.springboot.tienda.auth;


import com.mateo.springboot.tienda.dto.auth.LoginRequestDto;
import com.mateo.springboot.tienda.dto.auth.LoginResponseDto;
import com.mateo.springboot.tienda.dto.user.UserRegisterDto;
import com.mateo.springboot.tienda.models.User;
import com.mateo.springboot.tienda.security.CustomUserDetails;
import com.mateo.springboot.tienda.security.JwtUtil;
import com.mateo.springboot.tienda.service.user.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final UserService userService;



    public AuthController(AuthenticationManager authenticationManager, JwtUtil jwtUtil, UserService userService) {
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
        this.userService = userService;
    }

    // REGISTER
    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody UserRegisterDto user) {
        userService.register(user);
        return ResponseEntity.ok("User registered successfully");
    }

    //login
    @PostMapping("/login")
    public LoginResponseDto login(@RequestBody LoginRequestDto loginRequest) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.getEmail(),
                            loginRequest.getPassword()
                    )
            );

            CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();  // devuelve email
            String token = jwtUtil.generateToken(userDetails.getUsername());

            return new LoginResponseDto(token , userDetails.getUsername());

        } catch (AuthenticationException e) {
            throw new RuntimeException("Invalid email or password");
        }
    }

}
