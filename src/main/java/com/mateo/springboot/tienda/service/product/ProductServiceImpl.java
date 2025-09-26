package com.mateo.springboot.tienda.service.product;


import com.mateo.springboot.tienda.dto.product.ProductCreateDto;
import com.mateo.springboot.tienda.dto.product.ProductDto;
import com.mateo.springboot.tienda.dto.product.ProductUpdateDto;
import com.mateo.springboot.tienda.mapper.ProductMapper;
import com.mateo.springboot.tienda.models.Category;
import com.mateo.springboot.tienda.models.Product;
import com.mateo.springboot.tienda.repository.CategoryRepository;
import com.mateo.springboot.tienda.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductServiceImpl implements  ProductService{


    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;

    @Value("${product.low-stock-threshold}")
    private int lowStockThreshold;

    public ProductServiceImpl(ProductRepository productRepository, CategoryRepository categoryRepository) {
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
    }

    @Override
    public List<ProductDto> getProducts() {
        return productRepository.findAll().stream().map(ProductMapper::toDto).toList();
    }

    @Override
    public ProductDto getProductById(Long id) {
        Product product = productRepository.findById(id).orElseThrow( () -> new RuntimeException("Product not found"));
        return ProductMapper.toDto(product);
    }

    @Override
    public ProductDto createProduct(ProductCreateDto dto) {
        Category category = categoryRepository.findById(dto.getCategoryId()).orElseThrow(() -> new RuntimeException("Category not found"));
        Product product = ProductMapper.toEntity(dto,category);
        Product savedProduct = productRepository.save(product);
        return ProductMapper.toDto(savedProduct);
    }

    @Override
    public ProductDto updateProduct(Long id,ProductUpdateDto dto) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found"));
        Category category = null;
        if (dto.getCategoryId() != null) {
            category = categoryRepository.findById(dto.getCategoryId())
                    .orElseThrow(() -> new RuntimeException("Category not found"));
        }
        ProductMapper.updateProduct(product,dto,category);
        Product updatedProduct = productRepository.save(product);
        return ProductMapper.toDto(updatedProduct) ;
    }

    @Override
    public void deleteProduct(Long id) {
        if (!productRepository.existsById(id)){
            throw  new RuntimeException("Product not found");
        }
        productRepository.deleteById(id);
    }

    //-----------------stock-----------------
    public void adjustStock(Long productId, int quantityChange) {
        Product product = productRepository.findById(productId).orElseThrow(() -> new RuntimeException("Product Not found"));
        int newStock = product.getStock() + quantityChange;
        if (newStock < 0) {
            throw new RuntimeException("Not enough stock for product " + product.getName());
        }
        product.setStock(newStock);
        productRepository.save(product);
    }

    public void increaseStock(Long productId, int quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be positive to increase stock");
        }
        adjustStock(productId, quantity);
    }

    public void decreaseStock(Long productId, int quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be positive to decrease stock");
        }
        adjustStock(productId, -quantity);
    }

    public  int getStock(Long productId){
        Product product = productRepository.findById(productId).orElseThrow(() -> new RuntimeException("Product not found"));
        return product.getStock();
    }

    public  boolean isStockAvailable(Long productId, int quantity){
        Product product = productRepository.findById(productId).orElseThrow(() -> new RuntimeException("Product not found"));
        return  product.getStock() >= quantity;
    }

    public List<ProductDto>listLowStockProducts(){
        return  productRepository.findByStockLessThanEqual(lowStockThreshold).stream().map(ProductMapper::toDto).toList();
    }



}
