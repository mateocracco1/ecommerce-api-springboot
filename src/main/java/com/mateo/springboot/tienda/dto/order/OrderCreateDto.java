package com.mateo.springboot.tienda.dto.order;

import java.util.List;

public class OrderCreateDto {

    private Long userId;
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
