package com.mateo.springboot.tienda.controller;

import com.mateo.springboot.tienda.dto.cart.AddToCartRequest;
import com.mateo.springboot.tienda.dto.cart.CartResponseDTO;
import com.mateo.springboot.tienda.dto.cart.UpdateCartItemRequestDTO;
import com.mateo.springboot.tienda.mapper.CartMapper;
import com.mateo.springboot.tienda.models.Cart;
import com.mateo.springboot.tienda.models.User;
import com.mateo.springboot.tienda.security.CustomUserDetails;
import com.mateo.springboot.tienda.service.cart.CartService;
import com.mateo.springboot.tienda.service.user.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
@RequestMapping("/api/carts")
public class CartController {

    private final CartMapper cartMapper;
    private  final CartService cartService;
    private final UserService userService;


    public CartController(CartMapper cartMapper, CartService cartService, UserService userService) {
        this.cartMapper = cartMapper;
        this.cartService = cartService;
        this.userService = userService;
    }

    @GetMapping
    public CartResponseDTO getActiveCart(@AuthenticationPrincipal CustomUserDetails user) {
        User userEntity = userService.findUserOrThrow(user.getId());

        Cart cart = cartService.getActiveCart(userEntity);
        BigDecimal subtotal = cartService.calculateSubtotal(cart);

        return cartMapper.toDto(cart, subtotal);
    }

    @PostMapping("/items")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void addProduct(@AuthenticationPrincipal CustomUserDetails user,@Valid @RequestBody AddToCartRequest request) {
        User userEntity = userService.findUserOrThrow(user.getId());
        cartService.addProduct(userEntity, request.getProductId(), request.getQuantity());
    }

    @PutMapping("/items")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public  void updateItem(@AuthenticationPrincipal CustomUserDetails user,@Valid @RequestBody UpdateCartItemRequestDTO updateCartItemRequestDTO){
        User userEntity = userService.findUserOrThrow(user.getId());
        cartService.updateProductQuantity(userEntity,updateCartItemRequestDTO.getProductId(),updateCartItemRequestDTO.getQuantity());
    }

    @DeleteMapping("/items/{productId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteItem(@AuthenticationPrincipal CustomUserDetails user,@PathVariable Long productId){
        User userEntity = userService.findUserOrThrow(user.getId());
        cartService.removeProduct(userEntity,productId);
    }

}
