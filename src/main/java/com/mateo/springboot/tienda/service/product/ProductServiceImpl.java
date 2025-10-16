package com.mateo.springboot.tienda.service.product;


import com.mateo.springboot.tienda.dto.product.ProductCreateDto;
import com.mateo.springboot.tienda.dto.product.ProductDto;
import com.mateo.springboot.tienda.dto.product.ProductUpdateDto;
import com.mateo.springboot.tienda.exceptions.product.CategoryNotFoundException;
import com.mateo.springboot.tienda.exceptions.product.InvalidStockQuantityException;
import com.mateo.springboot.tienda.exceptions.product.ProductNotFoundException;
import com.mateo.springboot.tienda.exceptions.product.ProductOutOfStockException;
import com.mateo.springboot.tienda.mapper.ProductMapper;
import com.mateo.springboot.tienda.models.Category;
import com.mateo.springboot.tienda.models.Product;
import com.mateo.springboot.tienda.repository.CategoryRepository;
import com.mateo.springboot.tienda.repository.ProductRepository;
import jakarta.transaction.Transactional;
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
    public List<ProductDto> findAllProducts() {
        return productRepository.findAll().stream().map(ProductMapper::toDto).toList();
    }

    @Override
    public ProductDto findProductById(Long id) {
        Product product = findProductOrThrow(id);
        return ProductMapper.toDto(product);
    }

    @Override
    public ProductDto createProduct(ProductCreateDto dto) {
        Category category = categoryRepository.findById(dto.getCategoryId()).orElseThrow(() -> new CategoryNotFoundException(dto.getCategoryId()));
        Product product = ProductMapper.toEntity(dto,category);
        Product savedProduct = productRepository.save(product);
        return ProductMapper.toDto(savedProduct);
    }

    @Override
    public ProductDto updateProduct(Long id,ProductUpdateDto dto) {
        Product product =findProductOrThrow(id);
        Category category = null;
        if (dto.getCategoryId() != null) {
            category = categoryRepository.findById(dto.getCategoryId())
                    .orElseThrow(() -> new CategoryNotFoundException(dto.getCategoryId()));
        }
        ProductMapper.updateProduct(product,dto,category);
        Product updatedProduct = productRepository.save(product);
        return ProductMapper.toDto(updatedProduct) ;
    }

    @Transactional
    @Override
    public void deleteProductById(Long id) {
        Product product = findProductOrThrow(id);
        productRepository.delete(product);
    }

    //-----------------stock-----------------


    @Override
    public void adjustStock(Long productId, int quantityChange) {
            Product product =findProductOrThrow(productId);
            int newStock = product.getStock() + quantityChange;
            if (newStock < 0) {
                throw new ProductOutOfStockException(product.getName());
            }
            product.setStock(newStock);
            productRepository.save(product);
    }

    @Override
    public void increaseStock(Long productId, int quantity) {
        if (quantity <= 0) {
            throw new InvalidStockQuantityException(quantity);
        }
        adjustStock(productId, quantity);
    }

    @Override
    public void decreaseStock(Long productId, int quantity) {
        if (quantity <= 0) {
            throw new InvalidStockQuantityException(quantity);
        }
        adjustStock(productId, -quantity);
    }

    @Override
    public int getProductStock(Long productId) {
        Product product =findProductOrThrow(productId);
        return product.getStock();
    }

    @Override
    public boolean isStockAvailable(Long productId, int quantity) {
        Product product =findProductOrThrow(productId);
        return  product.getStock() >= quantity;
    }

    @Override
    public List<ProductDto> listLowStockProducts() {
        return  productRepository.findByStockLessThanEqual(lowStockThreshold).stream().map(ProductMapper::toDto).toList();
    }

    @Override
    public Product findProductOrThrow(Long productId) {
        return   productRepository.findById(productId).orElseThrow(() -> new ProductNotFoundException(productId));
    }
}
