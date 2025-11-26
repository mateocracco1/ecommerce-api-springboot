package com.mateo.springboot.tienda.controller;

import com.mateo.springboot.tienda.dto.order.OrderCreateDto;
import com.mateo.springboot.tienda.dto.order.OrderDetailCreateDto;
import com.mateo.springboot.tienda.dto.order.OrderDetailDto;
import com.mateo.springboot.tienda.dto.order.OrderDto;

import com.mateo.springboot.tienda.models.OrderStatus;
import com.mateo.springboot.tienda.service.order.OrderService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.MediaType;
import org.mockito.Mockito;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.web.servlet.MockMvc;

import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.context.bean.override.mockito.MockitoBean;


import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@WebMvcTest(OrderController.class)
@AutoConfigureMockMvc(addFilters = false)
class OrderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private OrderService orderService;

    @Test
    void getOrders_ShouldReturnAllOrders() throws Exception {

        OrderDetailDto detail = new OrderDetailDto(
                1L,
                "Mouse",
                2,
                new BigDecimal("2500"),
                new BigDecimal("5000")
        );


        OrderDto order = new OrderDto(
                1L,
                10L,
                "mateo",
                LocalDate.of(2024, 5, 10),
                new BigDecimal("5000"),
                OrderStatus.PAID,
                List.of(detail)
        );
        List<OrderDto> orders = List.of(order);
        when(orderService.findAllOrders()).thenReturn(orders);

        when(orderService.findAllOrders()).thenReturn(orders);

        mockMvc.perform(get("/api/orders"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].username").value("mateo"))
                .andExpect(jsonPath("$[0].total").value(5000))
                .andExpect(jsonPath("$[0].details[0].productName").value("Mouse"));

        verify(orderService).findAllOrders();
    }

    @Test
    void getOrderById_ShuldReturnOrderById() throws Exception {

        OrderDetailDto detail = new OrderDetailDto(
                1L,
                "Mouse",
                2,
                new BigDecimal("2500"),
                new BigDecimal("5000")
        );


        OrderDto order = new OrderDto(
                1L,
                10L,
                "mateo",
                LocalDate.of(2024, 5, 10),
                new BigDecimal("5000"),
                OrderStatus.PAID,
                List.of(detail)
        );

        when(orderService.findOrderById(1L)).thenReturn(order);

        mockMvc.perform(get("/api/orders/{id}",1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));

        verify(orderService).findOrderById(1L);
    }

    @Test
    void createOrder_ShouldReturnCreateOrder() throws Exception {

        OrderDetailDto returnedDetail1 = new OrderDetailDto(
                1L, "Mouse", 1, new BigDecimal("2500"), new BigDecimal("2500")
        );
        OrderDetailDto returnedDetail2 = new OrderDetailDto(
                2L, "Teclado", 1, new BigDecimal("5000"), new BigDecimal("5000")
        );

        OrderDto returnedOrder = new OrderDto(
                10L,
                100L,
                "mateo",
                LocalDate.of(2024, 10, 20),
                new BigDecimal("7500"),
                OrderStatus.PENDING,
                List.of(returnedDetail1, returnedDetail2)
        );
        when(orderService.createOrder(any(OrderCreateDto.class))).thenReturn(returnedOrder);


        mockMvc.perform(
                        post("/api/orders")
                                .contentType(APPLICATION_JSON)
                                .content("""
                        {
                          "userId": 100,
                          "details": [
                            {"productId": 1, "quantity": 1},
                            {"productId": 2, "quantity": 1}
                          ]
                        }
                    """)
                )
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(10))
                .andExpect(jsonPath("$.details.length()").value(2))
                .andExpect(jsonPath("$.details[0].productId").value(1))
                .andExpect(jsonPath("$.details[1].productId").value(2));

        verify(orderService).createOrder(any(OrderCreateDto.class));

    }

    @Test
    void deleteOrder() throws Exception {
            mockMvc.perform(delete("/api/orders/{id}",1L))
                    .andExpect(status().isNoContent());
        Mockito.verify(orderService).deleteOrder(1L);
    }
}