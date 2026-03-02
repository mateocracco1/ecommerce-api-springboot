package com.mateo.springboot.tienda.exceptions.order;

import com.mateo.springboot.tienda.exceptions.BusinessException;
import org.springframework.http.HttpStatus;

public class InvalidateTranstionException extends BusinessException {


    public InvalidateTranstionException() {
        super("You cannot change the status of a cancelled order.", "INVALID_TRANSITION", HttpStatus.BAD_REQUEST);
    }





}
