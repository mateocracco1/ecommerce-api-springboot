package com.mateo.springboot.tienda.exceptions.product;

import com.mateo.springboot.tienda.exceptions.BusinessException;
import org.springframework.http.HttpStatus;

public class ProductOutOfStockException extends BusinessException {


    public ProductOutOfStockException(String name) {
        super(String.format("Product %s out of stock",name ), "PRODUCT_OUT_OF_STOCK", HttpStatus.CONFLICT);
    }
}
