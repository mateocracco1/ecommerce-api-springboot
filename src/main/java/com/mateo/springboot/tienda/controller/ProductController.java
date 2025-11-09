package com.mateo.springboot.tienda.controller;

import com.mateo.springboot.tienda.dto.product.ProductCreateDto;
import com.mateo.springboot.tienda.dto.product.ProductDto;
import com.mateo.springboot.tienda.dto.product.ProductUpdateDto;
import com.mateo.springboot.tienda.service.product.ProductService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/products")
public class ProductController {


    private  final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }


    @GetMapping
    public ResponseEntity<List<ProductDto>>getProducts(){
        return  ResponseEntity.ok(productService.findAllProducts());
    }

    @GetMapping("/{id}")
    public  ResponseEntity<ProductDto>getProductById(@PathVariable Long id){
        return ResponseEntity.ok(productService.findProductById(id));
    }

    @PostMapping
    public ResponseEntity<ProductDto>createProduct(@Valid @RequestBody ProductCreateDto productCreateDto){
        ProductDto productDto = productService.createProduct(productCreateDto);
        return  ResponseEntity.status(HttpStatus.CREATED).body(productDto);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProductDto> updateProduct( @PathVariable Long id,@Valid @RequestBody ProductUpdateDto productUpdateDto) {
        ProductDto updatedProduct = productService.updateProduct(id, productUpdateDto);
        return ResponseEntity.ok(updatedProduct);
    }

    @DeleteMapping("/{id}")
    public  ResponseEntity<Void>deleteProduct(@PathVariable Long id){
        productService.deleteProductById(id);
        return ResponseEntity.noContent().build();
    }

}
