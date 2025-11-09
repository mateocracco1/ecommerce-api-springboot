package com.mateo.springboot.tienda.exceptions.product;

import com.mateo.springboot.tienda.exceptions.BusinessException;
import org.springframework.http.HttpStatus;

public class InvalidProductDataException extends BusinessException {


    public InvalidProductDataException() {
        super("The price must be greater than zero.", "PRICE_LESS_THAN_ZERO", HttpStatus.BAD_REQUEST);
    }

    public InvalidProductDataException(String message, String code) {
        super(message, code, HttpStatus.BAD_REQUEST);
    }

}
