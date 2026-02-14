package com.mateo.springboot.tienda.controller;

import com.mateo.springboot.tienda.dto.product.ProductCreateDto;
import com.mateo.springboot.tienda.dto.product.ProductDto;
import com.mateo.springboot.tienda.dto.product.ProductUpdateDto;
import com.mateo.springboot.tienda.mapper.ProductMapper;
import com.mateo.springboot.tienda.repository.ProductRepository;
import com.mateo.springboot.tienda.service.product.ProductService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/products")
public class ProductController {


    private  final ProductService productService;
    private final Logger log  = LoggerFactory.getLogger(ProductController.class);
    private final ProductMapper productMapper;


    public ProductController(ProductService productService, ProductMapper productMapper) {
        this.productService = productService;
        this.productMapper = productMapper;
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','CUSTOMER')")
    public ResponseEntity<List<ProductDto>>getProducts(){
        return  ResponseEntity.ok(productService.findAllProducts().stream().map(productMapper::toDto).toList());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','CUSTOMER')")
    public  ResponseEntity<ProductDto>getProductById(@PathVariable Long id){
        log.info("GET /api/products/{} - Fetching product ", id);
        return ResponseEntity.ok(productMapper.toDto(productService.findProductById(id)));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ProductDto>createProduct(@Valid @RequestBody ProductCreateDto productCreateDto){
        log.info("POST/api/products/{} - Creating Product", productCreateDto.getName());
        ProductDto productDto = productMapper.toDto(productService.createProduct(productCreateDto));
        return  ResponseEntity.status(HttpStatus.CREATED).body(productDto);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ProductDto> updateProduct( @PathVariable Long id,@Valid @RequestBody ProductUpdateDto productUpdateDto) {
        log.info("PUT /api/products/{} - Updating product", productUpdateDto.getName());
        ProductDto updatedProduct = productMapper.toDto(productService.updateProduct(id, productUpdateDto));
        return ResponseEntity.ok(updatedProduct);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public  ResponseEntity<Void>deleteProduct(@PathVariable Long id){
        log.info("DELETE /api/prodcuts/{} - Deleting products", id);
        productService.deleteProductById(id);
        return ResponseEntity.noContent().build();
    }

}
