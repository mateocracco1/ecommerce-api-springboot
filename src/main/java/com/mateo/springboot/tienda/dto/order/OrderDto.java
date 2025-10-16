package com.mateo.springboot.tienda.dto.order;

import com.mateo.springboot.tienda.models.OrderStatus;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public class OrderDto {

    private Long id;
    private Long userId;
    private String username;
    private LocalDate date;
    private BigDecimal total;
    private OrderStatus status;
    private List<OrderDetailDto> details;


    public OrderDto() {
    }

    public OrderDto(Long id, Long userId, String username, LocalDate date, BigDecimal total, OrderStatus status, List<OrderDetailDto> details) {
        this.id = id;
        this.userId = userId;
        this.username = username;
        this.date = date;
        this.total = total;
        this.status = status;
        this.details = details;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public BigDecimal getTotal() {
        return total;
    }

    public void setTotal(BigDecimal total) {
        this.total = total;
    }

    public List<OrderDetailDto> getDetails() {
        return details;
    }

    public void setDetails(List<OrderDetailDto> details) {
        this.details = details;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public void setStatus(OrderStatus status) {
        this.status = status;
    }
}
