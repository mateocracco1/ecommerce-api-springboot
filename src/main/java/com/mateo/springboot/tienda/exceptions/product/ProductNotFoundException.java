package com.mateo.springboot.tienda.exceptions.product;

import com.mateo.springboot.tienda.exceptions.BusinessException;
import org.springframework.http.HttpStatus;

public class ProductNotFoundException extends BusinessException {


    public ProductNotFoundException(Long productId) {
        super(String.format("Product with ID %d not found",productId),"PRODUCT_NOT_FOUND" , HttpStatus.NOT_FOUND);
    }
    public ProductNotFoundException(String name) {
        super(String.format("Product with name %s not found",name),"PRODUCT_NOT_FOUND" , HttpStatus.NOT_FOUND);
    }



}
