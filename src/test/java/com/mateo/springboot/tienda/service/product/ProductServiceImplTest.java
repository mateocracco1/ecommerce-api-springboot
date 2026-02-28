package com.mateo.springboot.tienda.service.product;

import com.mateo.springboot.tienda.dto.product.ProductCreateDto;
import com.mateo.springboot.tienda.dto.product.ProductDto;
import com.mateo.springboot.tienda.dto.product.ProductUpdateDto;
import com.mateo.springboot.tienda.exceptions.product.*;
import com.mateo.springboot.tienda.mapper.ProductMapper;
import com.mateo.springboot.tienda.models.Category;
import com.mateo.springboot.tienda.models.Product;
import com.mateo.springboot.tienda.repository.CategoryRepository;
import com.mateo.springboot.tienda.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;



@ExtendWith(MockitoExtension.class)
class ProductServiceImplTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private ProductMapper productMapper;

    @InjectMocks
    private ProductServiceImpl productService;

    private Product product;
    private Category category;
    private ProductCreateDto createDto;
    private ProductUpdateDto updateDto;



    @BeforeEach
    void setUp() {




        // Setup common entities and objects
        category = new Category(1L, "Electronics", "Desc");
        product = new Product(101L, "Laptop", new BigDecimal("1200.00"), 25, category);

        createDto = new ProductCreateDto();
        createDto.setName("NewPhone");
        createDto.setPrice(new BigDecimal("800.00"));
        createDto.setCategoryId(1L);
        createDto.setCategoryName("Electronics");

        updateDto = new ProductUpdateDto();
        updateDto.setName("UpdatedLaptop");
        updateDto.setPrice(new BigDecimal("1300.00"));
    }

    // --- findProductOrThrow Helper Test (Implied by other tests) ---

    @Test
    @DisplayName("findProductOrThrow should throw InvalidProductIdException for null or non-positive ID")
    void findProductOrThrow_shouldThrowInvalidProductIdException() {
        assertThrows(InvalidProductIdException.class, () -> productService.findProductOrThrow(null));
        assertThrows(InvalidProductIdException.class, () -> productService.findProductOrThrow(0L));
    }

    @Test
    @DisplayName("findProductOrThrow should throw ProductNotFoundException when product does not exist")
    void findProductOrThrow_shouldThrowProductNotFoundExceptionWhenNotFound() {
        when(productRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ProductNotFoundException.class, () -> productService.findProductOrThrow(999L));
        verify(productRepository, times(1)).findById(999L);
    }

    // --- findAllProducts Tests ---

    @Test
    @DisplayName("findAllProducts should return a list of all products")
    void findAllProducts_shouldReturnAllProducts() {
        List<Product> expectedProducts = Arrays.asList(product, new Product(102L, "Mouse", BigDecimal.TEN, 50, category));
        when(productRepository.findAll()).thenReturn(expectedProducts);

        List<Product> result = productService.findAllProducts();

        assertNotNull(result);
        assertEquals(2, result.size());
        verify(productRepository, times(1)).findAll();
    }

    // --- findProductById Tests ---

    @Test
    @DisplayName("findProductById should return the product when found")
    void findProductById_shouldReturnProductWhenExists() {
        when(productRepository.findById(101L)).thenReturn(Optional.of(product));

        Product result = productService.findProductById(101L);

        assertNotNull(result);
        assertEquals(101L, result.getId());
        assertEquals("Laptop", result.getName());
        verify(productRepository, times(1)).findById(101L);
    }

    // --- createProduct Tests ---

//    @Test
//    @DisplayName("createProduct should create product successfully with valid data")
//    void createProduct_shouldCreateProductSuccessfully() {
//        when(productRepository.existsByName("NewPhone")).thenReturn(false);
//        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));
//        when(productMapper.toEntity(any(ProductCreateDto.class), eq(category))).thenReturn(new Product(null, "NewPhone", new BigDecimal("800.00"), 0, category));
//        when(productRepository.save(any(Product.class))).thenReturn(new Product(200L, "NewPhone", new BigDecimal("800.00"), 0, category));
//
//        Product result = productService.createProduct(createDto);
//
//        assertNotNull(result);
//        assertEquals(200L, result.getId());
//        assertEquals("NewPhone", result.getName());
//        verify(productRepository, times(1)).existsByName("NewPhone");
//        verify(categoryRepository, times(1)).findById(1L);
//        verify(productRepository, times(1)).save(any(Product.class));
//    }

    @Test
    @DisplayName("createProduct should throw ProductAlreadyExistsException if name exists")
    void createProduct_shouldThrowProductAlreadyExistsExceptionWhenNameExists() {
        when(productRepository.existsByName("NewPhone")).thenReturn(true);

        assertThrows(ProductAlreadyExistsException.class, () -> productService.createProduct(createDto));
        verify(productRepository, times(1)).existsByName("NewPhone");
        verify(categoryRepository, never()).findById(anyLong());
    }

    @Test
    @DisplayName("createProduct should throw CategoryNotFoundException if category ID is invalid")
    void createProduct_shouldThrowCategoryNotFoundExceptionWhenCategoryMissing() {
        when(productRepository.existsByName("NewPhone")).thenReturn(false);
        when(categoryRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(CategoryNotFoundException.class, () -> productService.createProduct(createDto));
        verify(productRepository, times(1)).existsByName("NewPhone");
        verify(categoryRepository, times(1)).findById(1L);
        verify(productRepository, never()).save(any(Product.class));
    }

    @Test
    @DisplayName("createProduct should throw InvalidProductDataException if price is zero")
    void createProduct_shouldThrowInvalidProductDataExceptionWhenPriceIsZero() {
        createDto.setPrice(BigDecimal.ZERO);
        when(productRepository.existsByName("NewPhone")).thenReturn(false);
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));

        assertThrows(InvalidProductDataException.class, () -> productService.createProduct(createDto));
        verify(productRepository, times(1)).existsByName("NewPhone");
        verify(categoryRepository, times(1)).findById(1L);
        verify(productRepository, never()).save(any(Product.class));
    }

    @Test
    @DisplayName("createProduct should throw InvalidProductDataException if price is null")
    void createProduct_shouldThrowInvalidProductDataExceptionWhenPriceIsNull() {
        createDto.setPrice(null);
        when(productRepository.existsByName("NewPhone")).thenReturn(false);
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));

        assertThrows(InvalidProductDataException.class, () -> productService.createProduct(createDto));
        verify(productRepository, times(1)).existsByName("NewPhone");
        verify(categoryRepository, times(1)).findById(1L);
        verify(productRepository, never()).save(any(Product.class));
    }
    
    @Test
    @DisplayName("createProduct should throw InvalidProductDataException if category name mismatches ID")
    void createProduct_shouldThrowInvalidProductDataExceptionWhenCategoryNameMismatchesId() {
        createDto.setCategoryName("DifferentCategory");
        when(productRepository.existsByName("NewPhone")).thenReturn(false);
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category)); // category.getName() is "Electronics"

        assertThrows(InvalidProductDataException.class, () -> productService.createProduct(createDto));
        verify(productRepository, times(1)).existsByName("NewPhone");
        verify(categoryRepository, times(1)).findById(1L);
        verify(productRepository, never()).save(any(Product.class));
    }

    // --- updateProduct Tests ---

    @Test
    @DisplayName("updateProduct should update product successfully with new name and category")
    void updateProduct_shouldUpdateProductSuccessfully() {
        Long productId = 101L;
        when(productRepository.findById(productId)).thenReturn(Optional.of(product));
        when(productRepository.existsByName("UpdatedLaptop")).thenReturn(false);
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));
        
        // Mock mapper to simulate update logic
        Product updatedProduct = new Product(productId, "UpdatedLaptop", new BigDecimal("1300.00"), 25, category);
        when(productRepository.save(any(Product.class))).thenReturn(updatedProduct);

        Product result = productService.updateProduct(productId, updateDto);

        assertNotNull(result);
        assertEquals("UpdatedLaptop", result.getName());
        verify(productRepository, times(1)).findById(productId);
        verify(productRepository, times(1)).existsByName("UpdatedLaptop");
        verify(categoryRepository, times(1)).findById(1L);
        verify(productRepository, times(1)).save(any(Product.class));
    }
    
    @Test
    @DisplayName("updateProduct should succeed when updating fields other than name/category and name is not changing")
    void updateProduct_shouldSucceedWhenOnlyPriceIsUpdated() {
        Long productId = 101L;
        ProductUpdateDto priceOnlyDto = new ProductUpdateDto();
        priceOnlyDto.setPrice(new BigDecimal("1500.00"));
        
        when(productRepository.findById(productId)).thenReturn(Optional.of(product));
        
        // Mock mapper to simulate update logic
        Product updatedProduct = new Product(productId, "Laptop", new BigDecimal("1500.00"), 25, category);
        when(productRepository.save(any(Product.class))).thenReturn(updatedProduct);

        Product result = productService.updateProduct(productId, priceOnlyDto);

        assertNotNull(result);
        assertEquals(new BigDecimal("1500.00"), result.getPrice());
        verify(productRepository, times(1)).findById(productId);
        verify(productRepository, never()).existsByName(anyString()); // Should not check existence if name isn't provided
        verify(categoryRepository, never()).findById(anyLong());
        verify(productRepository, times(1)).save(any(Product.class));
    }

    @Test
    @DisplayName("updateProduct should throw ProductAlreadyExistsException if new name already exists")
    void updateProduct_shouldThrowProductAlreadyExistsExceptionIfNewNameExists() {
        Long productId = 101L;
        when(productRepository.findById(productId)).thenReturn(Optional.of(product));
        when(productRepository.existsByName("UpdatedLaptop")).thenReturn(true); // New name exists

        assertThrows(ProductAlreadyExistsException.class, () -> productService.updateProduct(productId, updateDto));
        verify(productRepository, times(1)).findById(productId);
        verify(productRepository, times(1)).existsByName("UpdatedLaptop");
        verify(categoryRepository, never()).findById(anyLong());
    }
    
    @Test
    @DisplayName("updateProduct should throw CategoryNotFoundException if new category ID is invalid")
    void updateProduct_shouldThrowCategoryNotFoundExceptionIfNewCategoryIsInvalid() {
        Long productId = 101L;
        updateDto.setCategoryId(99L);
        when(productRepository.findById(productId)).thenReturn(Optional.of(product));
        when(productRepository.existsByName("UpdatedLaptop")).thenReturn(false);
        when(categoryRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(CategoryNotFoundException.class, () -> productService.updateProduct(productId, updateDto));
        verify(productRepository, times(1)).findById(productId);
        verify(productRepository, times(1)).existsByName("UpdatedLaptop");
        verify(categoryRepository, times(1)).findById(99L);
    }

    // --- deleteProductById Tests ---

    @Test
    @DisplayName("deleteProductById should delete the product successfully")
    void deleteProductById_shouldDeleteProductSuccessfully() {
        Long productId = 101L;
        when(productRepository.findById(productId)).thenReturn(Optional.of(product));

        productService.deleteProductById(productId);

        verify(productRepository, times(1)).findById(productId);
        verify(productRepository, times(1)).delete(product);
    }

    // --- Stock Management Tests ---

    @Test
    @DisplayName("adjustStock should decrease stock correctly for a valid decrease")
    void adjustStock_shouldDecreaseStockWhenValidQuantityProvided() {
        Long productId = 101L;
        int quantityChange = -5; // Decrease by 5 (current stock 25 -> 20)
        when(productRepository.findById(productId)).thenReturn(Optional.of(product));
        Product productAfterChange = new Product(productId, "Laptop", product.getPrice(), 20, category);
        when(productRepository.save(any(Product.class))).thenReturn(productAfterChange);

        productService.adjustStock(productId, quantityChange);

        ArgumentCaptor<Product> productCaptor = ArgumentCaptor.forClass(Product.class);
        verify(productRepository, times(1)).findById(productId);
        verify(productRepository, times(1)).save(productCaptor.capture());
        assertEquals(20, productCaptor.getValue().getStock());
    }

    @Test
    @DisplayName("adjustStock should throw ProductOutOfStockException if stock goes below zero")
    void adjustStock_shouldThrowProductOutOfStockExceptionWhenStockGoesNegative() {
        Long productId = 101L;
        int quantityChange = -30; // Current stock 25 -> -5
        Product lowStockProduct = new Product(productId, "Laptop", product.getPrice(), 25, category);
        when(productRepository.findById(productId)).thenReturn(Optional.of(lowStockProduct));

        assertThrows(ProductOutOfStockException.class, () -> productService.adjustStock(productId, quantityChange));

        verify(productRepository, times(1)).findById(productId);
        verify(productRepository, never()).save(any(Product.class));
    }
    
    @Test
    @DisplayName("adjustStock should throw InvalidStockQuantityException if quantityChange is zero")
    void adjustStock_shouldThrowInvalidStockQuantityExceptionWhenQuantityChangeIsZero() {
        assertThrows(InvalidStockQuantityException.class, () -> productService.adjustStock(101L, 0));
        verify(productRepository, never()).findById(anyLong());
    }

    @Test
    @DisplayName("increaseStock should successfully increase stock by positive quantity")
    void increaseStock_shouldSucceed() {
        Long productId = 101L;
        int quantity = 10; // Current stock 25 -> 35
        when(productRepository.findById(productId)).thenReturn(Optional.of(product));
        Product productAfterChange = new Product(productId, "Laptop", product.getPrice(), 35, category);
        when(productRepository.save(any(Product.class))).thenReturn(productAfterChange);

        productService.increaseStock(productId, quantity);

        ArgumentCaptor<Product> productCaptor = ArgumentCaptor.forClass(Product.class);
        verify(productRepository, times(1)).findById(productId);
        verify(productRepository, times(1)).save(productCaptor.capture());
        assertEquals(35, productCaptor.getValue().getStock());
    }

    @Test
    @DisplayName("decreaseStock should successfully decrease stock by positive quantity")
    void decreaseStock_shouldSucceed() {
        Long productId = 101L;
        int quantity = 10; // Current stock 25 -> 15
        when(productRepository.findById(productId)).thenReturn(Optional.of(product));
        Product productAfterChange = new Product(productId, "Laptop", product.getPrice(), 15, category);
        when(productRepository.save(any(Product.class))).thenReturn(productAfterChange);

        productService.decreaseStock(productId, quantity);

        ArgumentCaptor<Product> productCaptor = ArgumentCaptor.forClass(Product.class);
        verify(productRepository, times(1)).findById(productId);
        verify(productRepository, times(1)).save(productCaptor.capture());
        assertEquals(15, productCaptor.getValue().getStock());
    }
    
    @Test
    @DisplayName("isStockAvailable should return true if stock is sufficient")
    void isStockAvailable_shouldReturnTrueIfSufficient() {
        Long productId = 101L;
        when(productRepository.findById(productId)).thenReturn(Optional.of(product)); // Stock 25

        assertTrue(productService.isStockAvailable(productId, 25));
        verify(productRepository, times(1)).findById(productId);
    }

    @Test
    @DisplayName("isStockAvailable should return false if stock is insufficient")
    void isStockAvailable_shouldReturnFalseIfInsufficient() {
        Long productId = 101L;
        when(productRepository.findById(productId)).thenReturn(Optional.of(product)); // Stock 25

        assertFalse(productService.isStockAvailable(productId, 26));
        verify(productRepository, times(1)).findById(productId);
    }

    @Test
    @DisplayName("isStockAvailable should throw InvalidStockQuantityException for quantity <= 0")
    void isStockAvailable_shouldThrowExceptionForInvalidQuantity() {
        assertThrows(InvalidStockQuantityException.class, () -> productService.isStockAvailable(101L, 0));
        assertThrows(InvalidStockQuantityException.class, () -> productService.isStockAvailable(101L, -5));
        verify(productRepository, never()).findById(anyLong());
    }
    
    // --- listLowStockProducts Tests ---

    @Test
    @DisplayName("listLowStockProducts should return products below or equal to threshold (10)")
    void listLowStockProducts_shouldReturnMatchingProducts() {
        Product lowStockProduct = new Product(103L, "Keyboard", new BigDecimal("50.00"), 5, category);
        List<Product> expected = Arrays.asList(lowStockProduct);

        when(productRepository.findByStockLessThanEqual(10)).thenReturn(expected);

        List<Product> result = productService.listLowStockProducts();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(103L, result.get(0).getId());
        verify(productRepository, times(1)).findByStockLessThanEqual(10);
    }
}