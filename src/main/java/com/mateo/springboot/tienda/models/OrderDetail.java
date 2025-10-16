package com.mateo.springboot.tienda.models;

import jakarta.persistence.*;

import java.math.BigDecimal;

@Entity
@Table(name = "order_details")
public class OrderDetail {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;    // producto del detalle

    @ManyToOne
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;        // pedido al que pertenece

    @Column(nullable = false)
    private int quantity;       // cantidad del producto

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal unitPrice; // precio unitario al momento de la compra
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal subtotal;

//🔹 Concepto clave
//
//Order 1 → N OrderDetail → 1 Product
//
//Cada detalle sabe: qué producto es, cuántas unidades, precio unitario y subtotal.
//
//Esto rompe la relación muchos a muchos entre Order y Product.


    public OrderDetail() {
    }

    public OrderDetail(Long id, Product product, Order order, int quantity, BigDecimal unitPrice, BigDecimal subtotal) {
        this.id = id;
        this.product = product;
        this.order = order;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
        this.subtotal = subtotal;
    }


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Order getOrder() {
        return order;
    }

    public void setOrder(Order order) {
        this.order = order;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public BigDecimal getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(BigDecimal subtotal) {
        this.subtotal = subtotal;
    }

    public BigDecimal getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(BigDecimal unitPrice) {
        this.unitPrice = unitPrice;
    }
}
