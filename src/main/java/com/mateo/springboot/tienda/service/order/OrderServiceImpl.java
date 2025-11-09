package com.mateo.springboot.tienda.service.order;


import com.mateo.springboot.tienda.dto.order.OrderCreateDto;
import com.mateo.springboot.tienda.dto.order.OrderDetailCreateDto;
import com.mateo.springboot.tienda.dto.order.OrderDto;
import com.mateo.springboot.tienda.exceptions.order.DuplicateOrderProductException;
import com.mateo.springboot.tienda.exceptions.order.InvalidOrderIdException;
import com.mateo.springboot.tienda.exceptions.order.OrderNotFoundException;
import com.mateo.springboot.tienda.exceptions.product.InvalidProductIdException;
import com.mateo.springboot.tienda.exceptions.product.ProductOutOfStockException;
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
import com.mateo.springboot.tienda.service.user.UserService;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class OrderServiceImpl  implements OrderService {


    private final OrderRepository orderRepository;
    private final OrderMapper orderMapper;
    private final OrderDetailMapper orderDetailMapper;
    private final UserService userService;
    private final ProductService productService;

    public OrderServiceImpl(OrderRepository orderRepository,OrderMapper orderMapper,
                            OrderDetailMapper orderDetailMapper,
                            UserService userService,
                            ProductService productService) {
        this.orderRepository = orderRepository;
        this.orderMapper = orderMapper;
        this.orderDetailMapper = orderDetailMapper;
        this.userService = userService;
        this.productService = productService;
    }


    @Override
    public List<OrderDto> findAllOrders() {
        return orderRepository.findAll().stream().map(orderMapper::toDto).toList();
    }

    @Override
    public OrderDto findOrderById(Long orderId) {
        Order order = findOrderOrThrow(orderId);
        return orderMapper.toDto(order);
    }

    @Override
    @Transactional
    public OrderDto createOrder(OrderCreateDto dto) {

        User user = userService.findUserOrThrow(dto.getUserId());


        Set<Long> productIds = new HashSet<>();

        for (OrderDetailCreateDto detailDto : dto.getDetails()) {

            if (!productIds.add(detailDto.getProductId())) {
                throw new DuplicateOrderProductException(detailDto.getProductId());
            }

            // Validar stock disponible ANTES de restar
            if (!productService.isStockAvailable(detailDto.getProductId(), detailDto.getQuantity())) {
                throw new ProductOutOfStockException("");
            }
        }

        // Crear orden
        Order order = orderMapper.fromCreateDto(dto, user);

        List<OrderDetail> details = new ArrayList<>();

        // Ahora sí descontar stock y crear detalles
        for (OrderDetailCreateDto detailDto : dto.getDetails()) {
            Product product = productService.findProductOrThrow(detailDto.getProductId());

            productService.decreaseStock(detailDto.getProductId(), detailDto.getQuantity());

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
        Order order =findOrderOrThrow(orderId);

        for (OrderDetail orderDetail : order.getDetails()){
            productService.increaseStock(orderDetail.getProduct().getId(),orderDetail.getQuantity());
        }
        orderRepository.delete(order);
    }

    private Order findOrderOrThrow(Long orderId){
        if (orderId == null || orderId <= 0){
            throw new InvalidOrderIdException();
        }
        return orderRepository.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException(orderId));
    }

}
