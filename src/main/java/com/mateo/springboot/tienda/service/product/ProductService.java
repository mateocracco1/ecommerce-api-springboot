package com.mateo.springboot.tienda.service.product;


import com.mateo.springboot.tienda.dto.pageDto.PageDto;
import com.mateo.springboot.tienda.dto.product.ProductCreateDto;
import com.mateo.springboot.tienda.dto.product.ProductDto;
import com.mateo.springboot.tienda.dto.product.ProductUpdateDto;
import com.mateo.springboot.tienda.models.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ProductService {

    PageDto<ProductDto> findAllProducts(Pageable pageable);
    ProductDto findProductById(Long id);
    ProductDto createProduct(ProductCreateDto dto);
    ProductDto updateProduct(Long id,ProductUpdateDto dto);
    void  deleteProductById(Long id);

    void decreaseStock(Long productId, int quantity);
    void increaseStock(Long productId, int quantity);
    int getProductStock(Long productId);
    boolean isStockAvailable(Long productId, int quantity);

    Page<ProductDto> listLowStockProducts(Pageable pageable);
    void adjustStock(Long productId, int quantityChange);

    Product findProductOrThrow(Long productId);
}
