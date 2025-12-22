package com.mateo.springboot.tienda.service.order;

import com.mateo.springboot.tienda.dto.order.OrderCreateDto;
import com.mateo.springboot.tienda.dto.order.OrderDetailCreateDto;
import com.mateo.springboot.tienda.dto.order.OrderDto;
import com.mateo.springboot.tienda.exceptions.order.OrderNotFoundException;
import com.mateo.springboot.tienda.mapper.OrderDetailMapper;
import com.mateo.springboot.tienda.mapper.OrderMapper;
import com.mateo.springboot.tienda.models.Order;
import com.mateo.springboot.tienda.models.OrderDetail;
import com.mateo.springboot.tienda.models.Product;
import com.mateo.springboot.tienda.models.User;
import com.mateo.springboot.tienda.repository.OrderRepository;
import com.mateo.springboot.tienda.service.product.ProductService;
import com.mateo.springboot.tienda.service.user.UserService;
import org.aspectj.weaver.ast.Or;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import java.time.LocalDate;


import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceImplTest {


    @InjectMocks
    private OrderServiceImpl orderService;

    @Mock
    private OrderRepository orderRepository;
    @Mock
    private OrderMapper orderMapper;
    @Mock
    private OrderDetailMapper orderDetailMapper;
    @Mock
    private UserService userService;
    @Mock
    private ProductService productService;

    private User testUser;
    private Product testProductA;
    private OrderCreateDto validOrderDto;
    private Order createdOrder;

    @BeforeEach
    void setUp() {

        testUser = new User();
        testUser.setId(1L);

        testProductA = new Product();
        testProductA.setId(100L);
        testProductA.setPrice(new BigDecimal("10.00"));
        // Aquí no necesitamos simular el stock real, solo simular si está disponible.

        OrderDetailCreateDto detailDto = new OrderDetailCreateDto(100L, 2);

        validOrderDto = new OrderCreateDto( List.of(detailDto));
        createdOrder = new Order(); // Objeto que simula la Order después del mapping
    }


    @Test
    void createOrder_ShouldSaveOrderAndDecreaseStock_WhenValid() {

        when(userService.findUserOrThrow(1L)).thenReturn(testUser);
        when(productService.findProductOrThrow(100L)).thenReturn(testProductA);
        when(productService.isStockAvailable(100L, 2)).thenReturn(true);

        when(orderMapper.fromCreateDto(any(OrderCreateDto.class), eq(testUser))).thenReturn(createdOrder);

        // 5. Simular el mapeo del detalle
        OrderDetail mockDetail = new OrderDetail();
        mockDetail.setProduct(testProductA);
        mockDetail.setQuantity(2);

        // --- ⚠️ CORRECCIÓN AQUÍ ⚠️ ---
        // Usamos setUnitPrice(), que existe en tu entidad OrderDetail
        mockDetail.setUnitPrice(new BigDecimal("10.00"));

        // También debemos simular el subtotal que calculará el mapper
        mockDetail.setSubtotal(new BigDecimal("20.00"));
        // -----------------------------

        when(orderDetailMapper.fromCreateDto(any(OrderDetailCreateDto.class), eq(testProductA), eq(createdOrder)))
                .thenReturn(mockDetail);

        // 6. Simular el guardado en el repositorio (devolver la misma orden para el mapeo final)
        when(orderRepository.save(eq(createdOrder))).thenReturn(createdOrder);

        // 7. Simular el mapeo final a DTO (no es estrictamente necesario, pero completa el ciclo)
        when(orderMapper.toDto(eq(createdOrder))).thenReturn(mock(com.mateo.springboot.tienda.dto.order.OrderDto.class));

        // ACT
//        orderService.createOrder(validOrderDto);

        // ASSERT (Verificar interacciones)
        // Verificar que el stock se haya validado
        verify(productService, times(1)).isStockAvailable(100L, 2);

        // Verificar que el stock se haya descontado
        verify(productService, times(1)).decreaseStock(100L, 2);

        // Verificar que se guardó la orden
        verify(orderRepository, times(1)).save(createdOrder);

    }

    @Test
    void createOrder_ShouldReturnCorrectTotal_WithBigDecimal() {

        Long userId = 1L;
        Long prod1 = 10L;
        Long prod2 = 20L;

        OrderDetailCreateDto d1 = new OrderDetailCreateDto(prod1, 2);
        OrderDetailCreateDto d2 = new OrderDetailCreateDto(prod2, 1);
        OrderCreateDto dto = new OrderCreateDto( List.of(d1, d2));

        User user = new User();

        Product p1 = new Product();
        p1.setPrice(new BigDecimal("100.00"));

        Product p2 = new Product();
        p2.setPrice(new BigDecimal("50.00"));

        Order order = new Order();

        OrderDetail od1 = new OrderDetail();
        od1.setProduct(p1);
        od1.setQuantity(2);
        od1.setUnitPrice(new BigDecimal("100.00"));
        od1.setSubtotal(new BigDecimal("200.00")); // 100 * 2

        OrderDetail od2 = new OrderDetail();
        od2.setProduct(p2);
        od2.setQuantity(1);
        od2.setUnitPrice(new BigDecimal("50.00"));
        od2.setSubtotal(new BigDecimal("50.00")); // 50 * 1

        // Total esperado: 100*2 + 50*1 = 250.00
        OrderDto expectedDto = new OrderDto();
        expectedDto.setId(99L);
        expectedDto.setUserId(1L);
        expectedDto.setTotal(new BigDecimal("250.00"));


        // Mocks
        when(userService.findUserOrThrow(userId)).thenReturn(user);

        when(productService.isStockAvailable(prod1, 2)).thenReturn(true);
        when(productService.isStockAvailable(prod2, 1)).thenReturn(true);

        when(orderMapper.fromCreateDto(dto, user)).thenReturn(order);

        when(productService.findProductOrThrow(prod1)).thenReturn(p1);
        when(productService.findProductOrThrow(prod2)).thenReturn(p2);

        when(orderDetailMapper.fromCreateDto(d1, p1, order)).thenReturn(od1);
        when(orderDetailMapper.fromCreateDto(d2, p2, order)).thenReturn(od2);

        when(orderRepository.save(order)).thenReturn(order);
        when(orderMapper.toDto(order)).thenReturn(expectedDto);


        // ACT
//        OrderDto result = orderService.createOrder(dto);


//        // ASSERT
//        assertNotNull(result);
//        assertEquals(0,
//                new BigDecimal("250.00").compareTo(result.getTotal()));
////        assertEquals(1L,dto.getUserId());

        verify(productService).decreaseStock(prod1, 2);
        verify(productService).decreaseStock(prod2, 1);
        verify(orderRepository).save(order);
    }


    @Test
    void findAllOrders_shouldReturnMappedDtos() {

        // Arrange
        Order order1 = new Order();
        Order order2 = new Order();

        OrderDto dto1 = new OrderDto();
        OrderDto dto2 = new OrderDto();

        List<Order> orders = List.of(order1, order2);
        List<OrderDto> expectedDtos = List.of(dto1, dto2);

        when(orderRepository.findAll()).thenReturn(orders);
        when(orderMapper.toDto(order1)).thenReturn(dto1);
        when(orderMapper.toDto(order2)).thenReturn(dto2);

        // Act
        List<OrderDto> result = orderService.findAllOrders();

        // Assert
        assertEquals(expectedDtos, result);

        verify(orderRepository).findAll();
        verify(orderMapper).toDto(order1);
        verify(orderMapper).toDto(order2);
    }



    @Test
    void findOrderById_shouldReturnOrderById(){

        Long id  = 1L;
        Order order = new Order();
        OrderDto orderDto = new OrderDto();

        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
        when(orderMapper.toDto(order)).thenReturn(orderDto);

        OrderDto result = orderService.findOrderById(id);

        assertEquals(orderDto, result);

        verify(orderRepository).findById(id);
        verify(orderMapper).toDto(order);
    }

    @Test
    void findOrderById_ShoudReturnOrderByIdNotFound(){

        Long id = 1L;

        assertThrows(OrderNotFoundException.class, () -> orderService.findOrderById(id));

        verify(orderRepository).findById(id);
        verifyNoInteractions(orderMapper); // opcional, pero recomendado
    }


    @Test
    void  deleteOrder_ShouldDeleteOrder(){

        // Arrange
        Long id = 2L;

        // OrderDetail 1
        OrderDetail detail1 = new OrderDetail();
        Product product1 = new Product();
        product1.setId(10L);
        detail1.setProduct(product1);
        detail1.setQuantity(3);

        // OrderDetail 2
        OrderDetail detail2 = new OrderDetail();
        Product product2 = new Product();
        product2.setId(20L);
        detail2.setProduct(product2);
        detail2.setQuantity(5);
        // Order with two details
        Order order = new Order();
        order.setDetails(List.of(detail1, detail2));

        when(orderRepository.findById(id)).thenReturn(Optional.of(order));

        // Act
        orderService.deleteOrder(id);

        // Assert
        verify(orderRepository).findById(id);

        // Verificar que se aumenta el stock de cada producto
        verify(productService).increaseStock(10L, 3);
        verify(productService).increaseStock(20L, 5);

        // Verificar que se elimina la orden
        verify(orderRepository).delete(order);

    }



    @Test
    void deleteOrder_ShouldExcpetionOrder(){

        Long id = 2L;
        when(orderRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(OrderNotFoundException.class,()->orderService.findOrderById(id));
        // Verifica que buscó la orden
        verify(orderRepository).findById(id);

        // NO debe llamar a aumentar stock
        verifyNoInteractions(productService);

        // NO debe intentar borrar la orden
        verify(orderRepository, never()).delete(any());


    }

}


