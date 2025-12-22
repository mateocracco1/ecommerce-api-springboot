package com.mateo.springboot.tienda.exceptions.user;

import com.mateo.springboot.tienda.exceptions.BusinessException;
import org.springframework.http.HttpStatus;

public class InvalidRoleException extends BusinessException {


    public InvalidRoleException(String role) {
        super(String.format("Role '%s' does not exist", role), "ROLE_NOT_FOUND", HttpStatus.NOT_FOUND);
    }
}
