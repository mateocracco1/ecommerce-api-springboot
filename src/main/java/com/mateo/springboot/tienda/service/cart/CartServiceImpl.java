package com.mateo.springboot.tienda.service.cart;

import com.mateo.springboot.tienda.exceptions.product.ProductNotFoundException;
import com.mateo.springboot.tienda.models.*;
import com.mateo.springboot.tienda.repository.CartItemRepository;
import com.mateo.springboot.tienda.repository.CartRepository;
import com.mateo.springboot.tienda.repository.ProductRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

// Impletnar LOGS

@Service
public class CartServiceImpl implements CartService{

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private  final ProductRepository productRepository;

    public CartServiceImpl(CartRepository cartRepository, CartItemRepository cartItemRepository, ProductRepository productRepository) {
        this.cartRepository = cartRepository;
        this.cartItemRepository = cartItemRepository;
        this.productRepository = productRepository;
    }

    @Transactional
    @Override
    public Cart getActiveCart(User user) {
        return cartRepository.findByUserAndStatus(user, StatusCart.ACTIVE).orElseGet(()-> {
            Cart cart = new Cart();
            cart.setUser(user);
            cart.setStatus(StatusCart.ACTIVE);
            return cartRepository.save(cart);
        });
    }

    @Transactional
    @Override
    public void addProduct(User user, Long productId, int quantity) {

        Cart cart = getActiveCart(user);

        Product product = productRepository.findById(productId).orElseThrow(() -> new ProductNotFoundException(productId));

        CartItem cartItem = cartItemRepository.findByCartAndProduct(cart,product).orElseGet(() -> {

            CartItem item = new CartItem();
            item.setCart(cart);
            item.setProduct(product);
            item.setQuantity(0);
            item.setUnitPrice(product.getPrice());
            return item;
        });
        cartItem.setQuantity(cartItem.getQuantity() + quantity);
        cartItemRepository.save(cartItem);
    }

    @Transactional
    @Override
    public void updateProductQuantity(User user, Long productId, int quantity) {

        if (quantity < 0) {
            throw new IllegalArgumentException("The quantity cannot be less than 0");
        }

        Cart cart = getActiveCart(user);

        CartItem cartItem = cartItemRepository.findByCartAndProduct(cart,productRepository.findById(productId)
                .orElseThrow(() -> new ProductNotFoundException(productId)))
                .orElseThrow(() -> new RuntimeException("The product is not in the cart"));  // agregar Exception

        if (quantity == 0) {
            cartItemRepository.delete(cartItem);
            return ;
        }
        cartItem.setQuantity(quantity);
        cartItemRepository.save(cartItem);
    }

    @Transactional
    @Override
    public void removeProduct(User user, Long productId) {

        Cart cart = getActiveCart(user);
        Product product = productRepository.findById(productId).orElseThrow(() -> new ProductNotFoundException(productId));
        CartItem cartItem = cartItemRepository.findByCartAndProduct(cart,product)
                .orElseThrow(() ->  new  RuntimeException("The product is not in the cart"));

        cartItemRepository.delete(cartItem);

    }

    @Override
    public BigDecimal calculateSubtotal(Cart cart) {

        BigDecimal subtotal = BigDecimal.ZERO;

        for (CartItem item : cart.getItems()){
            BigDecimal itemTotal = item.getUnitPrice().multiply(BigDecimal.valueOf(item.getQuantity()));
            subtotal = subtotal.add(itemTotal);
       }
        return subtotal;
    }

    @Transactional(readOnly = true)
    @Override
    public List<CartItem> getItems(User user) {
        return getActiveCart(user).getItems();
    }
}
