package com.mateo.springboot.tienda.exceptions.order;

import com.mateo.springboot.tienda.exceptions.BusinessException;
import org.springframework.http.HttpStatus;

public class InvalidOrderIdException  extends BusinessException {


    public InvalidOrderIdException() {
        super("Order with ID cannot be null or less than or equal to zero", "INVALID_ORDER_ID", HttpStatus.BAD_REQUEST);
    }
}
