package com.mateo.springboot.tienda.exceptions.user;

import com.mateo.springboot.tienda.exceptions.BusinessException;
import org.springframework.http.HttpStatus;

public class UserNotFoundException extends BusinessException {

    public UserNotFoundException(Long userId) {
        super(String.format("User with ID %d not found", userId),
                "USER_NOT_FOUND", HttpStatus.NOT_FOUND);
    }

    public UserNotFoundException(String username) {
        super(String.format("User with username '%s' not found", username),
                "USER_NOT_FOUND", HttpStatus.NOT_FOUND);
    }
}
