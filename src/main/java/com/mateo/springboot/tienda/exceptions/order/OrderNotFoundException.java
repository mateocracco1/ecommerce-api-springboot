package com.mateo.springboot.tienda.exceptions.order;

import com.mateo.springboot.tienda.exceptions.BusinessException;
import org.springframework.http.HttpStatus;

public class OrderNotFoundException extends BusinessException {


    public OrderNotFoundException(Long orderId) {
        super(String.format("Order with ID %d not found",orderId), "ORDER_NOT_FOUND", HttpStatus.NOT_FOUND);
    }
}
