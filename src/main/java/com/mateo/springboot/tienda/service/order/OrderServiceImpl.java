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
import com.mateo.springboot.tienda.models.*;
import com.mateo.springboot.tienda.repository.OrderRepository;
import com.mateo.springboot.tienda.repository.ProductRepository;
import com.mateo.springboot.tienda.repository.UserRepository;
import com.mateo.springboot.tienda.service.cart.CartService;
import com.mateo.springboot.tienda.service.product.ProductService;
import com.mateo.springboot.tienda.service.product.ProductServiceImpl;
import com.mateo.springboot.tienda.service.user.UserService;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
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
    private  final CartService cartService;


    private final Logger log  = LoggerFactory.getLogger(OrderServiceImpl.class);


    public OrderServiceImpl(OrderRepository orderRepository, OrderMapper orderMapper,
                            OrderDetailMapper orderDetailMapper,
                            UserService userService,
                            ProductService productService, CartService cartService) {
        this.orderRepository = orderRepository;
        this.orderMapper = orderMapper;
        this.orderDetailMapper = orderDetailMapper;
        this.userService = userService;
        this.productService = productService;
        this.cartService = cartService;
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
    public OrderDto createOrder(OrderCreateDto dto , Long userId) {

        log.info("Attempting to create order for userId {}", userId);

        User user = userService.findUserOrThrow(userId);
        Set<Long> productIds = new HashSet<>();

        for (OrderDetailCreateDto detailDto : dto.getDetails()) {

            if (!productIds.add(detailDto.getProductId())) {
                log.warn("Duplicate productId {} detected in order request", detailDto.getProductId());
                throw new DuplicateOrderProductException(detailDto.getProductId());
            }

            // Validar stock disponible ANTES de restar
            if (!productService.isStockAvailable(detailDto.getProductId(), detailDto.getQuantity())) {
                log.warn("Not enough stock for productId {}. Requested={}", detailDto.getProductId(), detailDto.getQuantity());
                throw new ProductOutOfStockException("");
            }
        }
        log.info("All products validated for order creation . Proceeding with stock adjustment...");
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
        log.info("Order created  successfully with id {}", savedOrder.getId());
        return orderMapper.toDto(savedOrder);
    }

    @Override
    @Transactional
    public void deleteOrder(Long orderId) {
        log.info("Attempting to delete order with id  {}", orderId);

        Order order =findOrderOrThrow(orderId);

        for (OrderDetail orderDetail : order.getDetails()){
            productService.increaseStock(orderDetail.getProduct().getId(),orderDetail.getQuantity());
        }
        orderRepository.delete(order);
        log.info("Order deleted  successfully with id {}", orderId);

    }

    private Order findOrderOrThrow(Long orderId){

        if (orderId == null || orderId <= 0){
            log.warn("Invalid OrderId received: {}", orderId);
            throw new InvalidOrderIdException();
        }
        return orderRepository.findById(orderId)
                .orElseThrow(() -> { log.warn("Order not found with id {}", orderId);
                    return new OrderNotFoundException(orderId);});
    }


    @Override
    public boolean isOrderOwner(Long orderId, Long userId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException(orderId));

        return order.getUser().getId().equals(userId);
    }

    @Transactional
    @Override
    public Order checkout(Long  idUser) {

        User user = userService.findUserOrThrow(idUser);
        Cart cart = cartService.getActiveCart(user);

        if (cart.getItems().isEmpty()) {
            throw new RuntimeException("Cannot checkout an empty cart"); // Crear  excepción
        }
        Order order = new Order();
        order.setUser(user);
        order.setStatus(OrderStatus.CREATED);
        order.setDate(LocalDateTime.now());

        List<OrderDetail> details = new ArrayList<>();

        for (CartItem item : cart.getItems()){

            productService.decreaseStock(
                    item.getProduct().getId(),
                    item.getQuantity()
            );

            OrderDetail orderDetail = new OrderDetail();
            orderDetail.setOrder(order);
            orderDetail.setProduct(item.getProduct());
            orderDetail.setQuantity(item.getQuantity());
            orderDetail.setUnitPrice(item.getUnitPrice());

            details.add(orderDetail);
        }
        order.setDetails(details);
        order.setTotal(order.calculateTotal());

        Order save = orderRepository.save(order);

        cartService.completeCart(cart);

        return save;
    }
}
