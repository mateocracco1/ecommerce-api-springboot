package com.mateo.springboot.tienda.mapper;

import com.mateo.springboot.tienda.dto.cart.CartItemResponseDTO;
import com.mateo.springboot.tienda.dto.cart.CartResponseDTO;
import com.mateo.springboot.tienda.models.Cart;
import com.mateo.springboot.tienda.models.CartItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.math.BigDecimal;

@Mapper(componentModel = "spring", imports = {BigDecimal.class})
public interface CartMapper{



    @Mapping(source = "cart.id", target = "cartId")
    @Mapping(source = "subtotal", target = "subTotal")
    CartResponseDTO toDto(Cart cart, BigDecimal subtotal);



    @Mapping(source = "product.id", target = "productId")
    @Mapping(source = "product.name", target = "productName")
    @Mapping(target = "total", expression = "java(item.getUnitPrice().multiply(BigDecimal.valueOf(item.getQuantity())))")
    CartItemResponseDTO toItemDto(CartItem item);
}
