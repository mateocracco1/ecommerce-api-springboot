package com.mateo.springboot.tienda.dto.cart;

import com.mateo.springboot.tienda.models.StatusCart;

import java.math.BigDecimal;
import java.util.List;

public class CartResponseDTO {


    private Long cartId;
    private StatusCart status;
    private List<CartItemResponseDTO> items;
    private BigDecimal subTotal;


    public Long getCartId() {
        return cartId;
    }

    public void setCartId(Long cartId) {
        this.cartId = cartId;
    }

    public StatusCart getStatus() {
        return status;
    }

    public void setStatus(StatusCart status) {
        this.status = status;
    }

    public BigDecimal getSubTotal() {
        return subTotal;
    }

    public void setSubTotal(BigDecimal subTotal) {
        this.subTotal = subTotal;
    }


    public List<CartItemResponseDTO> getItems() {
        return items;
    }

    public void setItems(List<CartItemResponseDTO> items) {
        this.items = items;
    }
}
