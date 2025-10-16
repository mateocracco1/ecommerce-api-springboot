package com.mateo.springboot.tienda.exceptions.product;

import com.mateo.springboot.tienda.exceptions.BusinessException;
import org.springframework.http.HttpStatus;

public class InvalidStockQuantityException extends BusinessException {

    public InvalidStockQuantityException(int quantity) {
        super(String.format("Invalid stock quantity: %d. Quantity must be greater than zero.", quantity),
                "INVALID_STOCK_QUANTITY", HttpStatus.BAD_REQUEST);
    }
}
