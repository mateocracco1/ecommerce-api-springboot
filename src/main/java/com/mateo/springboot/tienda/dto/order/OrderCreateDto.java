package com.mateo.springboot.tienda.dto.order;

import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

public class OrderCreateDto {

    @NotNull(message = "User ID is required")
    private Long userId;

    @NotEmpty(message = "Order must contain at least one product")
    @Valid
    private List<OrderDetailCreateDto> details;


    public OrderCreateDto() {
    }

    public OrderCreateDto(Long userId, List<OrderDetailCreateDto> details) {
        this.userId = userId;
        this.details = details;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public List<OrderDetailCreateDto> getDetails() {
        return details;
    }

    public void setDetails(List<OrderDetailCreateDto> details) {
        this.details = details;
    }
}
