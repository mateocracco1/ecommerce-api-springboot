package com.mateo.springboot.tienda.exceptions.order;

import com.mateo.springboot.tienda.exceptions.BusinessException;
import org.springframework.http.HttpStatus;

public class UnauthorizedOrderException extends BusinessException {


    public UnauthorizedOrderException() {
        super("You do not have permission to cancel this order.", "INVALID_ORDER", HttpStatus.BAD_REQUEST);
    }
}
