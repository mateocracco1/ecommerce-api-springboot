package com.mateo.springboot.tienda.dto.order;

import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

public class OrderCreateDto {


    // iduser lo saco de jwt


    @NotEmpty(message = "Order must contain at least one product")
    @Valid
    private List<OrderDetailCreateDto> details;


    public OrderCreateDto() {
    }

    public OrderCreateDto(List<OrderDetailCreateDto> details) {
        this.details = details;
    }


    public @NotEmpty(message = "Order must contain at least one product") @Valid List<OrderDetailCreateDto> getDetails() {
        return details;
    }

    public void setDetails(@NotEmpty(message = "Order must contain at least one product") @Valid List<OrderDetailCreateDto> details) {
        this.details = details;
    }
}
