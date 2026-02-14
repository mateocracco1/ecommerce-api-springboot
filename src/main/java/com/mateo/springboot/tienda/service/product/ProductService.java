package com.mateo.springboot.tienda.service.product;


import com.mateo.springboot.tienda.dto.product.ProductCreateDto;
import com.mateo.springboot.tienda.dto.product.ProductDto;
import com.mateo.springboot.tienda.dto.product.ProductUpdateDto;
import com.mateo.springboot.tienda.models.Product;

import java.util.List;

public interface ProductService {

    List<Product> findAllProducts();
    Product findProductById(Long id);
    Product createProduct(ProductCreateDto dto);
    Product updateProduct(Long id,ProductUpdateDto dto);
    void  deleteProductById(Long id);

    void decreaseStock(Long productId, int quantity);
    void increaseStock(Long productId, int quantity);
    int getProductStock(Long productId);
    boolean isStockAvailable(Long productId, int quantity);

    List<Product> listLowStockProducts();
    void adjustStock(Long productId, int quantityChange);

    Product findProductOrThrow(Long productId);
}
