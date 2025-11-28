package com.mateo.springboot.tienda.exceptions.user;

import com.mateo.springboot.tienda.exceptions.BusinessException;
import org.springframework.http.HttpStatus;

public class InvalidUserIdException extends BusinessException {


    public InvalidUserIdException() {
        super("User ID cannot be null or less than or equal to zero", "INVALID_USER_ID", HttpStatus.BAD_REQUEST);
    }



}
