package com.mateo.springboot.tienda.service.order;

import com.mateo.springboot.tienda.mapper.OrderMapper;
import com.mateo.springboot.tienda.models.*;
import com.mateo.springboot.tienda.repository.OrderRepository;
import com.mateo.springboot.tienda.repository.ProductRepository;
import com.mateo.springboot.tienda.repository.UserRepository;
import com.mateo.springboot.tienda.service.cart.CartService;
import com.mateo.springboot.tienda.service.product.ProductService;
import com.mateo.springboot.tienda.service.user.UserService;
import io.jsonwebtoken.JwsHeader;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceImplTest {

    @Mock
    private OrderRepository orderRepository;
    @Mock
    private OrderMapper orderMapper;
    @Mock
    private ProductService  productService;
    @Mock
    private CartService  cartService;

    @Mock
    private UserService userService;

    @InjectMocks
    private OrderServiceImpl orderServiceImpl;


    @Test
    void findAllOrders() {

        List<Order> orders = Arrays.asList(new Order(), new Order());

        when(orderRepository.findAll()).thenReturn(orders);

        List<Order> result = orderServiceImpl.findAllOrders();

        assertNotNull(result);
        assertEquals(2, result.size());

        verify(orderRepository).findAll();    }

    @Test
    void findOrderById() {
    }

    @Test
    void createOrder() {
    }

    @Test
    void deleteOrder() {
    }

    @Test
    void checkout() {

        Long userId = 1L;
// A. Creamos el usuario
        User mockUser = new User();
        mockUser.setId(userId);

        // B. Creamos un producto de prueba
        Product mockProduct = new Product();
        mockProduct.setId(99L); // ID del producto para verificar el stock

        // C. Creamos un item en el carrito con ese producto
        CartItem cartItem = new CartItem();
        cartItem.setProduct(mockProduct);
        cartItem.setQuantity(2);
        cartItem.setUnitPrice(new BigDecimal("500.00"));

        // D. Creamos el carrito y le metemos el item (para pasar el if)
        Cart mockCart = new Cart();
        mockCart.setUser(mockUser);
        mockCart.setItems(Arrays.asList(cartItem));

        // E. Creamos la orden falsa que nos devolverá el repositorio
        Order savedOrder = new Order();
        savedOrder.setId(100L);
        savedOrder.setStatus(OrderStatus.CREATED);

// F. Entrenamos a los Mocks (¡A todos los que devuelven algo!)
        when(userService.findUserOrThrow(userId)).thenReturn(mockUser);
        when(cartService.getActiveCart(mockUser)).thenReturn(mockCart);
        when(orderRepository.save(any(Order.class))).thenReturn(savedOrder);

        // Nota: productService.decreaseStock y cartService.completeCart son métodos 'void'
        // (no devuelven nada), así que no hace falta usar 'when' con ellos.

        // --- 2. ACT (¡Acción!) ---
        Order result = orderServiceImpl.checkout(userId);

        // --- 3. ASSERT (Validamos el resultado) ---
        assertNotNull(result);
        assertEquals(100L, result.getId());
        assertEquals(OrderStatus.CREATED, result.getStatus());

        // --- 4. VERIFY (Auditoría: Revisamos que haya llamado a todos sus amigos) ---
        verify(userService).findUserOrThrow(userId);
        verify(cartService).getActiveCart(mockUser);

        // Verificamos que bajó el stock exactamente del producto 99L y la cantidad 2
        verify(productService).decreaseStock(99L, 2);

        // Verificamos que se guardó una orden cualquiera y que se completó el carrito
        verify(orderRepository).save(any(Order.class));
        verify(cartService).completeCart(mockCart);
    }
    @Test
    void checkout_ThrowsException_WhenCartIsEmpty(){


        Long userId = 1L;

        User mockUser = new User();
        mockUser.setId(userId);

        Cart cart = new Cart();
        cart.setUser(mockUser);
        cart.setItems(new ArrayList<>());

        when(userService.findUserOrThrow(userId)).thenReturn(mockUser);
        when(cartService.getActiveCart(mockUser)).thenReturn(cart);

        RuntimeException exception = assertThrows(RuntimeException.class, () -> orderServiceImpl.checkout(userId));
        assertEquals("Cannot checkout an empty cart", exception.getMessage());


        verify(userService).findUserOrThrow(userId);
        verify(cartService).getActiveCart(mockUser);

        verify(orderRepository, never()).save(any(Order.class));
        verify(productService, never()).decreaseStock(anyLong(), anyInt());

    }


    @Test
    void isOrderOwner() {




    }
}