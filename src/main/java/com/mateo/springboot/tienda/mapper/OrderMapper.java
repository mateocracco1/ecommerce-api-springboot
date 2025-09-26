package com.mateo.springboot.tienda.mapper;

import com.mateo.springboot.tienda.dto.order.OrderCreateDto;
import com.mateo.springboot.tienda.dto.order.OrderDto;
import com.mateo.springboot.tienda.models.Order;
import com.mateo.springboot.tienda.models.OrderStatus;
import com.mateo.springboot.tienda.models.User;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.stream.Collectors;

@Component
public class OrderMapper {


    private final OrderDetailMapper detailMapper;

    public OrderMapper(OrderDetailMapper detailMapper) {
        this.detailMapper = detailMapper;
    }
    // entidad → DTO

    public  OrderDto toDto(Order order) {
        if (order == null) return null;

        OrderDto dto = new OrderDto();
        dto.setId(order.getId());
        dto.setDate(order.getDate());
        dto.setUsername(order.getUser().getUsername());
        dto.setUserId(order.getUser().getId());
        dto.setTotal(order.getTotal());
        dto.setStatus(order.getStatus());

        dto.setDetails(
                order.getDetails()
                        .stream()
                        .map(detailMapper::toDto)
                        .collect(Collectors.toList())
        );

        return dto;
    }
    // DTO de creación → entidad (sin detalles todavía, porque necesitan los productos)
    public Order fromCreateDto(OrderCreateDto dto, User user) {
        if (dto == null) return null;

        Order order = new Order();
        order.setUser(user);
        order.setDate(LocalDate.now());
        order.setTotal(java.math.BigDecimal.ZERO); // se recalcula al agregar detalles
        order.setStatus(OrderStatus.PENDING);
        return order;
    }
}
