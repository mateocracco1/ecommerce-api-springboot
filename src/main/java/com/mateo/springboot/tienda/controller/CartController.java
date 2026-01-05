package com.mateo.springboot.tienda.controller;

import com.mateo.springboot.tienda.dto.cart.CartItemResponseDTO;
import com.mateo.springboot.tienda.dto.cart.CartResponseDTO;
import com.mateo.springboot.tienda.dto.cart.UpdateCartItemRequestDTO;
import com.mateo.springboot.tienda.mapper.CartMapper;
import com.mateo.springboot.tienda.models.Cart;
import com.mateo.springboot.tienda.models.User;
import com.mateo.springboot.tienda.service.cart.CartService;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
@RequestMapping("/api/carts")
public class CartController {

    private final CartMapper cartMapper;
    private  final CartService cartService ;


    public CartController(CartMapper cartMapper, CartService cartService) {
        this.cartMapper = cartMapper;
        this.cartService = cartService;
    }

    @GetMapping
    public CartResponseDTO getActiveCart(@AuthenticationPrincipal User user) {

        Cart cart = cartService.getActiveCart(user);
        BigDecimal subtotal = cartService.calculateSubtotal(cart);

        return cartMapper.toDto(cart, subtotal);
    }

    @PostMapping("/items")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void addProduct(@AuthenticationPrincipal User user, @RequestBody CartItemResponseDTO request) {
        cartService.addProduct(user, request.getProductId(), request.getQuantity());
    }

    @PutMapping("/items")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public  void updateItem(@AuthenticationPrincipal User user, @RequestBody UpdateCartItemRequestDTO updateCartItemRequestDTO){

        cartService.updateProductQuantity(user,updateCartItemRequestDTO.getProductId(),updateCartItemRequestDTO.getQuantity());
    }



    @DeleteMapping("/items/{productId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteItem(@AuthenticationPrincipal User user,@PathVariable Long productId){
            cartService.removeProduct(user,productId);
    }

}
