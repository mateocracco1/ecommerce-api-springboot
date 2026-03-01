package com.mateo.springboot.tienda.controller;

import com.mateo.springboot.tienda.dto.order.OrderCreateDto;
import com.mateo.springboot.tienda.dto.order.OrderDto;
import com.mateo.springboot.tienda.mapper.OrderMapper;
import com.mateo.springboot.tienda.models.Order;
import com.mateo.springboot.tienda.security.CustomUserDetails;
import com.mateo.springboot.tienda.service.order.OrderService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService orderService;
    private final Logger log  = LoggerFactory.getLogger(OrderController.class);
    private final OrderMapper orderMapper;

    //Agregar ver mis ordenes

    public OrderController(OrderService orderService, OrderMapper orderMapper) {
        this.orderService = orderService;
        this.orderMapper = orderMapper;
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<OrderDto>>getOrders(){
        return ResponseEntity.ok(orderService.findAllOrders().stream().map(orderMapper::toDto).toList());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or @orderSecurity.isOwner(#id, authentication.principal.id)")
    public ResponseEntity<OrderDto>getOrderById(@PathVariable Long id){
        return ResponseEntity.ok(orderMapper.toDto(orderService.findOrderById(id)));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN','CUSTOMER')")
    public ResponseEntity<OrderDto>createOrder(@Valid @RequestBody OrderCreateDto orderCreateDto
            ,@AuthenticationPrincipal CustomUserDetails principal) { // principal obtengo id user

        log.info("POST/api/orders/{} - Creating Product for  user ", principal.getId());
        Order order = orderService.createOrder(orderCreateDto,principal.getId());
        OrderDto responseDto = orderMapper.toDto(order);

        return ResponseEntity.status(HttpStatus.CREATED).body(responseDto);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void>deleteOrder(@PathVariable Long id){
        log.info("DELETE /api/orders/{} - Deleting order ", id);
        orderService.deleteOrder(id);
        return ResponseEntity.noContent().build();
    }


    @PostMapping("/checkout")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<OrderDto> checkout(@AuthenticationPrincipal CustomUserDetails user) {
        Order newOrder = orderService.checkout(user.getId());
        OrderDto responseDto = orderMapper.toDto(newOrder);
        return  ResponseEntity.status(HttpStatus.CREATED).body(responseDto);
    }





    @GetMapping("/my-orders")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<Page<OrderDto>> getMyOrders(@AuthenticationPrincipal CustomUserDetails user, Pageable pageable ){
        Page<Order> ordersPage = orderService.findOrdersByUserId(user.getId(), pageable);
        return  ResponseEntity.ok(ordersPage.map(orderMapper::toDto)) ;
    }
}
