package com.mateo.springboot.tienda.exceptions.order;

import com.mateo.springboot.tienda.exceptions.BusinessException;
import org.springframework.http.HttpStatus;

public class EmptyCartException extends BusinessException {


    public EmptyCartException() {
        super("Cannot checkout an empty cart", "EMPTY_CART", HttpStatus.BAD_REQUEST);
    }
}
