package com.mateo.springboot.tienda.mapper;

import com.mateo.springboot.tienda.dto.order.OrderDetailCreateDto;
import com.mateo.springboot.tienda.dto.order.OrderDetailDto;
import com.mateo.springboot.tienda.models.Order;
import com.mateo.springboot.tienda.models.OrderDetail;
import com.mateo.springboot.tienda.models.Product;
import org.springframework.stereotype.Component;

@Component
public class OrderDetailMapper {

    public OrderDetailDto toDto(OrderDetail detail) {
        if (detail == null) return null;

        return new OrderDetailDto(
                detail.getProduct().getId(),
                detail.getProduct().getName(),
                detail.getQuantity(),
                detail.getUnitPrice(),
                detail.getSubtotal()
        );
    }


    // para crear un detalle desde el DTO de entrada
    public OrderDetail fromCreateDto(OrderDetailCreateDto dto, Product product, Order order) {
        if (dto == null) return null;

        OrderDetail detail = new OrderDetail();
        detail.setProduct(product);
        detail.setOrder(order);
        detail.setQuantity(dto.getQuantity());
        detail.setUnitPrice(product.getPrice());
        detail.setSubtotal(product.getPrice().multiply(
                java.math.BigDecimal.valueOf(dto.getQuantity())
        ));
        return detail;
    }



}
