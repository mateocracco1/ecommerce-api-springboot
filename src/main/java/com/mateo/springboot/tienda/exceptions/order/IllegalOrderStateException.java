package com.mateo.springboot.tienda.exceptions.order;

import com.mateo.springboot.tienda.exceptions.BusinessException;
import org.springframework.http.HttpStatus;

public class IllegalOrderStateException extends BusinessException {


    public IllegalOrderStateException() {
        super("An order that has already been submitted or completed cannot be cancelled.", "INVALID_ORDER", HttpStatus.BAD_REQUEST);
    }
}
