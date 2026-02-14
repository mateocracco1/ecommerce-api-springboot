package com.mateo.springboot.tienda.security;

import com.mateo.springboot.tienda.service.order.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class OrderSecurity {

    @Autowired
    private OrderService orderService;

    public boolean isOwner(Long orderId, Long userId) {
        return orderService.isOrderOwner(orderId, userId);
    }
}
