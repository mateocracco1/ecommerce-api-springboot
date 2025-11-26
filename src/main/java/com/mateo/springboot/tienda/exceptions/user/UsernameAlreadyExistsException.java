package com.mateo.springboot.tienda.exceptions.user;

import com.mateo.springboot.tienda.exceptions.BusinessException;
import org.springframework.http.HttpStatus;

public class UsernameAlreadyExistsException extends BusinessException {

    public UsernameAlreadyExistsException(String username ) {
        super( String.format("username '%s' already exists", username), "USERNAME_ALREADY_EXISTS", HttpStatus.CONFLICT);
    }
}
