package com.mateo.springboot.tienda.service.cart;

import com.mateo.springboot.tienda.models.Cart;
import com.mateo.springboot.tienda.models.CartItem;
import com.mateo.springboot.tienda.models.User;

import java.math.BigDecimal;
import java.util.List;

public interface CartService {

    Cart getActiveCart(User user);
    void addProduct(User user, Long productId, int quantity);
    void updateProductQuantity(User user, Long productId, int quantity);
    void removeProduct(User user, Long productId);
    BigDecimal calculateSubtotal(Cart cart);
    List<CartItem> getItems(User user);
    void completeCart(Cart cart);





}
