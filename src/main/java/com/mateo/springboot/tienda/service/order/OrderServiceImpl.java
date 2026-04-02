package com.mateo.springboot.tienda.service.order;


import com.mateo.springboot.tienda.dto.order.OrderCreateDto;
import com.mateo.springboot.tienda.dto.order.OrderDetailCreateDto;
import com.mateo.springboot.tienda.dto.order.OrderDto;
import com.mateo.springboot.tienda.exceptions.BusinessException;
import com.mateo.springboot.tienda.exceptions.order.*;
import com.mateo.springboot.tienda.mapper.OrderMapper;
import com.mateo.springboot.tienda.models.*;
import com.mateo.springboot.tienda.repository.OrderRepository;
import com.mateo.springboot.tienda.service.cart.CartService;
import com.mateo.springboot.tienda.service.product.ProductService;
import com.mateo.springboot.tienda.service.user.UserService;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class OrderServiceImpl  implements OrderService {


    private final OrderRepository orderRepository;
    private final OrderMapper orderMapper;

    private final UserService userService;
    private final ProductService productService;
    private  final CartService cartService;


    private final Logger log  = LoggerFactory.getLogger(OrderServiceImpl.class);


    public OrderServiceImpl(OrderRepository orderRepository, OrderMapper orderMapper,

                            UserService userService,
                            ProductService productService, CartService cartService) {
        this.orderRepository = orderRepository;
        this.orderMapper = orderMapper;

        this.userService = userService;
        this.productService = productService;
        this.cartService = cartService;
    }


    @Override
    public Page<OrderDto> findAllOrders(Pageable pageable) {
        return orderRepository.findAll(pageable).map(orderMapper::toDto);
    }

    @Override
    public OrderDto findOrderById(Long orderId) {
        Order order = findOrderOrThrow(orderId);
        return orderMapper.toDto(order);
    }

    private Order findOrderEntity(Long orderId) {
        return orderRepository.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException(orderId));
    }


    //Para compra rapida/now
    @Override
    @Transactional
    public OrderDto createOrder(OrderCreateDto dto , Long userId) {

        log.info("Attempting to create order for userId {}", userId);

        User user = userService.findUserOrThrow(userId);

        Order order = orderMapper.fromCreateDto(dto,user);

        List<OrderDetail> details = new ArrayList<>();
        Set<Long> productIds = new HashSet<>();

        for (OrderDetailCreateDto detailDto : dto.getDetails()) {

            if (!productIds.add(detailDto.getProductId())) {
                log.warn("Duplicate productId {} detected in order request", detailDto.getProductId());
                throw new DuplicateOrderProductException(detailDto.getProductId());
            }
            productService.decreaseStock(detailDto.getProductId(), detailDto.getQuantity());

            Product product = productService.findProductOrThrow(detailDto.getProductId());

            OrderDetail detail = orderMapper.fromDetailCreateDto(detailDto, product, order);
            detail.setUnitPrice(product.getPrice());
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


    @Transactional
    @Override
    public OrderDto checkout(Long  idUser) {

        User user = userService.findUserOrThrow(idUser);
        Cart cart = cartService.getActiveCart(user);

        if (cart.getItems().isEmpty()) {
            throw new EmptyCartException();
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

        return orderMapper.toDto(save);
    }


    @Override
    public boolean isOrderOwner(Long orderId, Long userId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException(orderId));

        return order.getUser().getId().equals(userId);
    }

    @Override
    public Page<OrderDto> findOrdersByUserId(Long userId, Pageable pageable) {
        Page<Order> ordersPage = orderRepository.findByUserId(userId, pageable);
        return ordersPage.map(orderMapper::toDto);
    }

    @Override
    public Page<OrderDto> findOrdersByUserIdAndStatus(Long userId, String status, Pageable pageable) {
        Page<Order> orderPage = orderRepository.findByUserIdAndStatus(userId,status,pageable);
        return orderPage.map(orderMapper::toDto);
    }




    @Override
    @Transactional
    public OrderDto cancelOrder(Long orderId , Long userId) {

        Order order = findOrderEntity(orderId);

        if (!order.getUser().getId().equals(userId)) {

            throw new UnauthorizedOrderException();
        }

        if (order.getStatus() == OrderStatus.SHIPPED || order.getStatus() == OrderStatus.COMPLETED
                || order.getStatus() == OrderStatus.CANCELLED) {
            throw new IllegalOrderStateException();
        }
        for (OrderDetail detail : order.getDetails()) {
            productService.increaseStock(
                    detail.getProduct().getId(),
                    detail.getQuantity()
            );
        }

        order.setStatus(OrderStatus.CANCELLED);
        return orderMapper.toDto(orderRepository.save(order));
    }

    @Override
    @Transactional
    public OrderDto updateOrderStatus(Long orderId, OrderStatus newStatus) {

        Order order = findOrderEntity(orderId);
        if (order.getStatus() == OrderStatus.CANCELLED) {
            throw new InvalidateTranstionException();
        }
        order.setStatus(newStatus);
        return orderMapper.toDto(orderRepository.save(order));
    }
}
