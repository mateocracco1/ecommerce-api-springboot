package com.mateo.springboot.tienda.controller;

import com.mateo.springboot.tienda.dto.order.OrderCreateDto;
import com.mateo.springboot.tienda.dto.order.OrderDto;
import com.mateo.springboot.tienda.service.order.OrderService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService orderService;
    private final Logger log  = LoggerFactory.getLogger(OrderController.class);


    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @GetMapping
    public ResponseEntity<List<OrderDto>>getOrders(){
        return ResponseEntity.ok(orderService.findAllOrders());
    }
    @GetMapping("/{id}")
    public ResponseEntity<OrderDto>getOrderById(@PathVariable Long id){
        return ResponseEntity.ok(orderService.findOrderById(id));
    }

    @PostMapping
    public ResponseEntity<OrderDto>createOrder(@Valid @RequestBody OrderCreateDto orderCreateDto){
        log.info("POST/api/orders/{} - Creating Product for  user ",orderCreateDto.getUserId());
        OrderDto orderDto = orderService.createOrder(orderCreateDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(orderDto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void>deleteOrder(@PathVariable Long id){
        log.info("DELETE /api/orders/{} - Deleting order ", id);
        orderService.deleteOrder(id);
        return ResponseEntity.noContent().build();
    }

}
