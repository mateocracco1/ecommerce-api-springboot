package com.mateo.springboot.tienda.exceptions.product;

import com.mateo.springboot.tienda.exceptions.BusinessException;
import org.springframework.http.HttpStatus;

public class CategoryNotFoundException extends BusinessException {


    public CategoryNotFoundException(Long categoryId) {
        super(String.format("Category with ID %d not found",categoryId), "CATEGORY_NOT_FOUND", HttpStatus.NOT_FOUND);
    }
}
