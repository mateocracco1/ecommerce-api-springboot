package com.mateo.springboot.tienda.dto.product;

import java.math.BigDecimal;

public class ProductUpdateDto {

    private String name;
    private String description;
    private BigDecimal price;
    private Integer stock;       // Integer para poder distinguir null de 0
    private Long categoryId;     // si quiere cambiar la categoría


    public ProductUpdateDto() {
    }

    public ProductUpdateDto(String name, String description, BigDecimal price, Integer stock, Long categoryId) {
        this.name = name;
        this.description = description;
        this.price = price;
        this.stock = stock;
        this.categoryId = categoryId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public Integer getStock() {
        return stock;
    }

    public void setStock(Integer stock) {
        this.stock = stock;
    }

    public Long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
    }
}
