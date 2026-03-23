package com.mateo.springboot.tienda.service.product;


import com.mateo.springboot.tienda.dto.pageDto.PageDto;
import com.mateo.springboot.tienda.dto.product.ProductCreateDto;
import com.mateo.springboot.tienda.dto.product.ProductDto;
import com.mateo.springboot.tienda.dto.product.ProductUpdateDto;
import com.mateo.springboot.tienda.exceptions.product.*;
import com.mateo.springboot.tienda.mapper.ProductMapper;
import com.mateo.springboot.tienda.models.Category;
import com.mateo.springboot.tienda.models.Product;
import com.mateo.springboot.tienda.repository.CategoryRepository;
import com.mateo.springboot.tienda.repository.ProductRepository;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;




@Service
public class ProductServiceImpl implements  ProductService{

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final Logger log  = LoggerFactory.getLogger(ProductServiceImpl.class);
    private final ProductMapper productMapper;


    @Value("${product.low-stock-threshold}")
    private int lowStockThreshold;

    public ProductServiceImpl(ProductRepository productRepository, CategoryRepository categoryRepository, ProductMapper productMapper) {
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
        this.productMapper = productMapper;
    }
    //probar

    @Cacheable(
            value = "product_list",
            key = "#pageable.pageNumber + '-' + #pageable.pageSize + '-' + #pageable.sort.toString()"
    )    @Override
    public PageDto<ProductDto> findAllProducts(Pageable pageable) {
        // 1. Buscamos en la BD (esto ya usa tu @EntityGraph)
        Page<Product> productPage = productRepository.findAll(pageable);

        // 2. Convertimos a DTOs
        List<ProductDto> productDtos = productPage.getContent()
                .stream()
                .map(productMapper::toDto)
                .toList();

        // 3. Devolvemos nuestro objeto serializable
        return new PageDto<>(
                productDtos,
                productPage.getNumber(),
                productPage.getSize(),
                productPage.getTotalElements(),
                productPage.getTotalPages(),
                productPage.isLast()
        );
    }

    @Cacheable(value = "product_details", key = "#id")
    @Override
    public ProductDto findProductById(Long id) {
        log.info("Request to find product by id {}", id);
        Product product = findProductOrThrow(id);
        return productMapper.toDto(product);
    }

    @Transactional
    @CacheEvict(value = {"product_list", "product_details"}, allEntries = true)
    @Override
    public ProductDto createProduct(ProductCreateDto dto) {

        log.info("Attempting to create product  with name: {}", dto.getName());

        if (productRepository.existsByName(dto.getName())) {
            log.warn("Name already exists: {}", dto.getName());
            throw new ProductAlreadyExistsException(dto.getName());
        }

        //para test / ya tiene validacione en Dto
        if (dto.getPrice() == null || dto.getPrice().compareTo(BigDecimal.ZERO) <= 0) {
            throw new InvalidProductDataException();
        }

        Category category = categoryRepository.findById(dto.getCategoryId())
                .orElseThrow(() -> {
                    log.warn("Category not found with id {}", dto.getCategoryId());
                    return new CategoryNotFoundException(dto.getCategoryId());
                });

        if (dto.getCategoryName() != null &&
                !dto.getCategoryName().equalsIgnoreCase(category.getName())) {
            log.warn("Category name '{}' does not match category id {}", dto.getCategoryName(), dto.getCategoryId());
            throw new InvalidProductDataException("Category name does not match Category ID", "CATEGORY_NOT_MATCH");
        }

        Product savedProduct = productRepository.save(productMapper.toEntity(dto,category));
        log.info("Product created  successfully with id {}", savedProduct.getId());
        return productMapper.toDto(savedProduct);
    }

    @Transactional
    @Caching(evict = {
            @CacheEvict(value = "product_list", allEntries = true),
            @CacheEvict(value = "product_details", key = "#id")
    })
    @Override
    public ProductDto updateProduct(Long id,ProductUpdateDto dto) {

        log.info("Attempting to update product with id: {}", id);
        Product product =findProductOrThrow(id);

        if (dto.getName() != null &&
                !dto.getName().equalsIgnoreCase(product.getName()) &&
                productRepository.existsByName(dto.getName())) {
            log.warn("Product already exists with name : {}", dto.getName());
            throw new ProductAlreadyExistsException(dto.getName());
        }

        Category category = null;
        if (dto.getCategoryId() != null) {
            category = categoryRepository.findById(dto.getCategoryId())
                    .orElseThrow(() -> {
                        log.warn("Category not found with id {}", dto.getCategoryId());
                        return new CategoryNotFoundException(dto.getCategoryId());
                    });
        }

        productMapper.updateProduct(product,dto,category);
        Product updatedProduct = productRepository.save(product);
        log.info("Product updated successfully with id: {}", id);

        return productMapper.toDto(updatedProduct) ;
    }

    @Caching(evict = {
            @CacheEvict(value = "product_list", allEntries = true),
            @CacheEvict(value = "product_details", key = "#id")
    })
    @Transactional
    @Override
    public void deleteProductById(Long id) {
        log.info("Attempting to delete product with id: {}", id);
        Product product = findProductOrThrow(id);
        productRepository.delete(product);
        log.info("Product deleted successfully with id: {}", id);
    }

    //-----------------stock product-----------------


    @Override
    public   void adjustStock(Long productId, int quantityChange) {
        log.info("Attempting to adjustStock  product with id: {}", productId);
        if(quantityChange == 0) {
            log.warn("Invalid stock change {} for product id {}", quantityChange, productId);
            throw new InvalidStockQuantityException(quantityChange);
            }

        Product product = productRepository.findByIdForUpdate(productId)
                .orElseThrow(() -> {
                    log.warn("Product not found with id {}", productId);
                    return new ProductNotFoundException(productId);
                });


        long total = (long) product.getStock() + quantityChange;
        if (total > Integer.MAX_VALUE) {
            log.warn("Stock overflow: current={}, change={}, productId={}", product.getStock(), quantityChange, productId);
            throw new InvalidStockQuantityException(quantityChange);
            }
        int newStock = (int) total;
        if(newStock < 0) {
            log.warn("Stock cannot go below zero. Current={}, change={}, productId={}", product.getStock(), quantityChange, productId);
            throw new ProductOutOfStockException(product.getName());
            }
        product.setStock(newStock);
        productRepository.save(product);
        log.info("Stock adjusted successfully for product id {}. New stock: {}", productId, newStock);

    }

    @Transactional
    @Caching(evict = {
            @CacheEvict(value = "product_list", allEntries = true),
            @CacheEvict(value = "product_details", key = "#productId")
    })
    @Override
    public void increaseStock(Long productId, int quantity) {

        log.info("Attempting to increaseStock  product with id: {}", productId);
        if (quantity <= 0) {
            log.warn("Invalid quantity {} for increaseStock, product id {}", quantity, productId);
            throw new InvalidStockQuantityException(quantity);
        }
        adjustStock(productId, quantity);

    }

    @Transactional
    @Caching(evict = {
            @CacheEvict(value = "product_list", allEntries = true),
            @CacheEvict(value = "product_details", key = "#productId")
    })
    @Override
    public void decreaseStock(Long productId, int quantity) {
        log.info("Attempting to decreaseStock  product with id: {}", productId);
        if (quantity <= 0) {
            log.warn("Invalid quantity {} for decreaseStock, product id {}", quantity, productId);
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
        log.info("Checking stock availability for product id {}", productId);

        if (quantity <= 0) {
            log.warn("Invalid quantity {} for stock check, product id {}", quantity, productId);
            throw new InvalidStockQuantityException(quantity);
        }
        Product product =findProductOrThrow(productId);
        boolean available = product.getStock() >= quantity;

        log.debug("Stock check result for product id {}: required={}, current={}, available={}",
                productId, quantity, product.getStock(), available);

        return  available;
    }

    @Override
    public Page<ProductDto> listLowStockProducts(Pageable pageable) {
        return productRepository.findByStockLessThanEqual(lowStockThreshold, pageable)
                .map(productMapper::toDto);
    }

    @Override
    public Product findProductOrThrow(Long productId) {

        if (productId == null || productId <= 0){
            log.warn("Invalid productId received: {}", productId);
            throw new InvalidProductIdException();
        }
        return   productRepository.findById(productId).orElseThrow(() ->{ log.warn("Product not found with id {}", productId);
            return new ProductNotFoundException(productId);
        });
    }
}
