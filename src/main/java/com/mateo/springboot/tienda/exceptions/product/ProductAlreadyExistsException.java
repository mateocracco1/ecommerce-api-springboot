package com.mateo.springboot.tienda.exceptions.product;

import com.mateo.springboot.tienda.exceptions.BusinessException;
import org.springframework.http.HttpStatus;

public class ProductAlreadyExistsException extends BusinessException {


    public ProductAlreadyExistsException(String name) {
        super( String.format("Product '%s' already exists", name), "PRODUCT_ALREADY_EXISTS", HttpStatus.CONFLICT);
    }



}
