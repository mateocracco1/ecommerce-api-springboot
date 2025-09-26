package com.mateo.springboot.tienda.service.order;

import com.mateo.springboot.tienda.dto.order.OrderCreateDto;
import com.mateo.springboot.tienda.dto.order.OrderDto;

import java.util.List;

public interface OrderService {


    List<OrderDto>getOrders();
    OrderDto getOrder(Long orderId);
    OrderDto createOrder(OrderCreateDto dto);
    void  deleteOrder(Long orderId);

}
