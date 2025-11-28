package com.mateo.springboot.tienda.exceptions.product;

import com.mateo.springboot.tienda.exceptions.BusinessException;
import org.springframework.http.HttpStatus;

public class InvalidProductIdException extends BusinessException {

    public InvalidProductIdException() {
        super("Product ID cannot be null or less than or equal to zero", "INVALID_PRODUCT_ID", HttpStatus.CONFLICT);
    }
}
