package com.mateo.springboot.tienda.service.product;


import com.mateo.springboot.tienda.dto.product.ProductCreateDto;
import com.mateo.springboot.tienda.dto.product.ProductDto;
import com.mateo.springboot.tienda.dto.product.ProductUpdateDto;

import java.util.List;

public interface ProductService {

    List<ProductDto> getProducts();
    ProductDto getProductById(Long id);
    ProductDto createProduct(ProductCreateDto dto);
    ProductDto updateProduct(Long id,ProductUpdateDto dto);
    void  deleteProduct(Long id);

    void decreaseStock(Long productId, int quantity);
    void increaseStock(Long productId, int quantity);
    int getStock(Long productId);
    boolean isStockAvailable(Long productId, int quantity);
}
