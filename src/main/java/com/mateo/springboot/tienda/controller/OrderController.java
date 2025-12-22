package com.mateo.springboot.tienda.controller;

import com.mateo.springboot.tienda.dto.order.OrderCreateDto;
import com.mateo.springboot.tienda.dto.order.OrderDto;
import com.mateo.springboot.tienda.security.CustomUserDetails;
import com.mateo.springboot.tienda.service.order.OrderService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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


    //Agregar ver mis ordenes

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<OrderDto>>getOrders(){
        return ResponseEntity.ok(orderService.findAllOrders());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or @orderSecurity.isOwner(#id, authentication.principal.id)")
    public ResponseEntity<OrderDto>getOrderById(@PathVariable Long id){
        return ResponseEntity.ok(orderService.findOrderById(id));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN','CUSTOMER')")
    public ResponseEntity<OrderDto>createOrder(@Valid @RequestBody OrderCreateDto orderCreateDto
            ,@AuthenticationPrincipal CustomUserDetails principal) { // principal obtengo id user

        log.info("POST/api/orders/{} - Creating Product for  user ", principal.getId());
        OrderDto orderDto = orderService.createOrder(orderCreateDto,principal.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(orderDto);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void>deleteOrder(@PathVariable Long id){
        log.info("DELETE /api/orders/{} - Deleting order ", id);
        orderService.deleteOrder(id);
        return ResponseEntity.noContent().build();
    }

}
