package com.mateo.springboot.tienda.service.order;

import com.mateo.springboot.tienda.dto.order.OrderCreateDto;
import com.mateo.springboot.tienda.dto.order.OrderDto;
import com.mateo.springboot.tienda.models.Order;
import com.mateo.springboot.tienda.models.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface OrderService {


    List<Order>findAllOrders();
    Order findOrderById(Long orderId);
    Order createOrder(OrderCreateDto dto, Long userId);
    void  deleteOrder(Long orderId);

    boolean isOrderOwner(Long orderId, Long userId);


    Order checkout(Long idUser);
    Page<Order> findOrdersByUserId(Long userId, Pageable pageable);
    Page<Order> findOrdersByUserIdAndStatus(Long userId, String status, Pageable pageable);

}
