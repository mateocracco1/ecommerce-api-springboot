package com.mateo.springboot.tienda.models;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "orders")
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "user_id",nullable = false)
    private User user;              // quién hizo el pedido

    private LocalDate date;         // fecha del pedido

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal total;           // total del pedido

    @Enumerated(EnumType.STRING) // <- Esto hace que se guarde como texto en la base de datos
    private OrderStatus status;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderDetail> details; // lista de productos en el pedido

    public Order() {
    }

    public Order(Long id, User user, LocalDate date, BigDecimal total, OrderStatus status, List<OrderDetail> details) {
        this.id = id;
        this.user = user;
        this.date = date;
        this.total = total;
        this.status = status;
        this.details = details;
    }

    public BigDecimal calculateTotal() {
        return details.stream()
                .map(OrderDetail::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }


    public BigDecimal getTotal() {
        return total;
    }

    public void setTotal(BigDecimal total) {
        this.total = total;
    }

    public List<OrderDetail> getDetails() {
        return details;
    }


    public OrderStatus getStatus() {
        return status;
    }

    public void setStatus(OrderStatus status) {
        this.status = status;
    }

    public void setDetails(List<OrderDetail> details) {
        this.details = details;
    }
}
