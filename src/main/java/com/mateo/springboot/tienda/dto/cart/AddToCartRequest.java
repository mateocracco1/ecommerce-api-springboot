package com.mateo.springboot.tienda.dto.cart;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public class AddToCartRequest {


    @NotNull(message = "El ID del producto es obligatorio")
    private Long productId;

    @Min(value = 1, message = "La cantidad debe ser al menos 1")
    private int quantity;

    // Constructores, Getters y Setters
    public AddToCartRequest() {}


    public @NotNull(message = "El ID del producto es obligatorio") Long getProductId() {
        return productId;
    }

    public void setProductId(@NotNull(message = "El ID del producto es obligatorio") Long productId) {
        this.productId = productId;
    }

    public @Min(value = 1, message = "La cantidad debe ser al menos 1") int getQuantity() {
        return quantity;
    }

    public void setQuantity(@Min(value = 1, message = "La cantidad debe ser al menos 1") int quantity) {
        this.quantity = quantity;
    }
}
