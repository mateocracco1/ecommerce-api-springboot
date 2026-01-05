package com.mateo.springboot.tienda.mapper;

import com.mateo.springboot.tienda.dto.cart.CartItemResponseDTO;
import com.mateo.springboot.tienda.dto.cart.CartResponseDTO;
import com.mateo.springboot.tienda.models.Cart;
import com.mateo.springboot.tienda.service.cart.CartService;

import java.math.BigDecimal;
import java.util.List;

public class CartMapper {




    public CartResponseDTO toDto(Cart cart, BigDecimal subtotal) {

        List<CartItemResponseDTO> items = cart.getItems().stream().map(cartItem -> {

            CartItemResponseDTO dto = new CartItemResponseDTO();
            dto.setProductId(cartItem.getProduct().getId());
            dto.setProductName(cartItem.getProduct().getName());
            dto.setUnitPrice(cartItem.getUnitPrice());
            dto.setQuantity(cartItem.getQuantity());
            dto.setTotal(cartItem.getUnitPrice().multiply(BigDecimal.valueOf(cartItem.getQuantity()))
            );

            return dto;
        }).toList();

        CartResponseDTO response = new CartResponseDTO();

        response.setCartId(cart.getId());
        response.setStatus(cart.getStatus());
        response.setItems(items);
        response.setSubTotal(subtotal);

        return response;

    }


}
