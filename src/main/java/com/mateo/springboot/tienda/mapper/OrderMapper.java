package com.mateo.springboot.tienda.mapper;


import com.mateo.springboot.tienda.dto.order.OrderCreateDto;
import com.mateo.springboot.tienda.dto.order.OrderDetailCreateDto;
import com.mateo.springboot.tienda.dto.order.OrderDetailDto;
import com.mateo.springboot.tienda.dto.order.OrderDto;
import com.mateo.springboot.tienda.models.*;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Mapper(componentModel = "spring", imports = {LocalDateTime.class, BigDecimal.class, OrderStatus.class})
public interface OrderMapper {



    //DTO
    @Mapping(source = "user.id", target = "userId")
    @Mapping(source = "user.username", target = "username")
    OrderDto toDto(Order order); // llama a toDetailDto internamente


    @Mapping(source = "product.id", target = "productId")
    @Mapping(source = "product.name", target = "productName")
    OrderDetailDto toDetailDto(OrderDetail detail);

    //ENTITY


    @Mapping(target = "id", ignore = true)
    @Mapping(target = "details", ignore = true)
    @Mapping(target = "user", source = "user")
    @Mapping(target = "date", expression = "java(LocalDateTime.now())")
    @Mapping(target = "total", expression = "java(BigDecimal.ZERO)")
    @Mapping(target = "status", expression = "java(OrderStatus.CREATED)")
    Order fromCreateDto(OrderCreateDto dto, User user);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "product", source = "product")
    @Mapping(target = "order", source = "order")
    @Mapping(target = "quantity", source = "dto.quantity")
    @Mapping(target = "unitPrice", source = "product.price")
    OrderDetail fromDetailCreateDto(OrderDetailCreateDto dto, Product product, Order order);




}
