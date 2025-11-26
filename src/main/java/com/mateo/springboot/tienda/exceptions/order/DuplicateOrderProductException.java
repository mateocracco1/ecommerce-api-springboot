package com.mateo.springboot.tienda.exceptions.order;

import com.mateo.springboot.tienda.exceptions.BusinessException;
import org.springframework.http.HttpStatus;

public class DuplicateOrderProductException extends BusinessException {


    public DuplicateOrderProductException(Long productId) {
        super(String.format("Product with ID %d is duplicated in the order", productId), "DUPLICATE_ORDER_PRODUCT", HttpStatus.BAD_REQUEST);
    }
}
