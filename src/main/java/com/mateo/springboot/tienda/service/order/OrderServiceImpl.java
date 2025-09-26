package com.mateo.springboot.tienda.service.order;


import com.mateo.springboot.tienda.dto.order.OrderCreateDto;
import com.mateo.springboot.tienda.dto.order.OrderDetailCreateDto;
import com.mateo.springboot.tienda.dto.order.OrderDto;
import com.mateo.springboot.tienda.mapper.OrderDetailMapper;
import com.mateo.springboot.tienda.mapper.OrderMapper;
import com.mateo.springboot.tienda.models.Order;
import com.mateo.springboot.tienda.models.OrderDetail;
import com.mateo.springboot.tienda.models.Product;
import com.mateo.springboot.tienda.models.User;
import com.mateo.springboot.tienda.repository.OrderRepository;
import com.mateo.springboot.tienda.repository.ProductRepository;
import com.mateo.springboot.tienda.repository.UserRepository;
import com.mateo.springboot.tienda.service.product.ProductService;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
public class OrderServiceImpl  implements OrderService {


    private final OrderRepository orderRepository;
    private final OrderMapper orderMapper;
    private final OrderDetailMapper orderDetailMapper;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final ProductService productService;

    public OrderServiceImpl(OrderRepository orderRepository, OrderMapper orderMapper, OrderDetailMapper orderDetailMapper, UserRepository userRepository, ProductRepository productRepository, ProductService productService) {
        this.orderRepository = orderRepository;
        this.orderMapper = orderMapper;
        this.orderDetailMapper = orderDetailMapper;
        this.userRepository = userRepository;
        this.productRepository = productRepository;
        this.productService = productService;
    }


    @Override
    public List<OrderDto> getOrders() {
        return orderRepository.findAll().stream().map(orderMapper::toDto).toList();
    }

    @Override
    public OrderDto getOrder(Long orderId) {
        Order order = orderRepository.findById(orderId).orElseThrow(() -> new RuntimeException("Order not found"));
        return orderMapper.toDto(order);
    }

    @Override
    @Transactional
    public OrderDto createOrder(OrderCreateDto dto) {

        User user = userRepository.findById(dto.getUserId()).orElseThrow(() -> new RuntimeException("User not found"));
        Order order = orderMapper.fromCreateDto(dto,user);


        List<OrderDetail> details = new ArrayList<>();

        for (OrderDetailCreateDto detailDto : dto.getDetails()) {
            Product product = productRepository.findById(detailDto.getProductId())
                    .orElseThrow(() -> new RuntimeException("Product not found"));

            productService.decreaseStock(detailDto.getProductId(),detailDto.getQuantity());

            OrderDetail detail = orderDetailMapper.fromCreateDto(detailDto, product, order);
            details.add(detail);


        }
        order.setDetails(details);
        order.setTotal(order.calculateTotal());
        Order savedOrder = orderRepository.save(order);
        return orderMapper.toDto(savedOrder);
    }

    @Override
    @Transactional
    public void deleteOrder(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));


        for (OrderDetail orderDetail : order.getDetails()){
            productService.increaseStock(orderDetail.getProduct().getId(),orderDetail.getQuantity());
        }
        orderRepository.delete(order);
    }
}
