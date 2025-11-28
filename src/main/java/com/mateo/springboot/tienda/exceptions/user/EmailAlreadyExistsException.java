package com.mateo.springboot.tienda.exceptions.user;

import com.mateo.springboot.tienda.exceptions.BusinessException;
import org.springframework.http.HttpStatus;

public class EmailAlreadyExistsException extends BusinessException {


    public EmailAlreadyExistsException(String email) {
        super(
                String.format("Email '%s' already exists", email), "EMAIL_ALREADY_EXISTS", HttpStatus.CONFLICT
        );
    }
}
