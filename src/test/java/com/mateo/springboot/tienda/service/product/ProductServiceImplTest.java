package com.mateo.springboot.tienda.service.product;

import com.mateo.springboot.tienda.dto.product.ProductCreateDto;
import com.mateo.springboot.tienda.dto.product.ProductDto;
import com.mateo.springboot.tienda.dto.product.ProductUpdateDto;
import com.mateo.springboot.tienda.exceptions.product.InvalidProductDataException;
import com.mateo.springboot.tienda.exceptions.product.InvalidStockQuantityException;
import com.mateo.springboot.tienda.exceptions.product.ProductNotFoundException;
import com.mateo.springboot.tienda.models.Category;
import com.mateo.springboot.tienda.models.Product;
import com.mateo.springboot.tienda.models.User;
import com.mateo.springboot.tienda.repository.CategoryRepository;
import com.mateo.springboot.tienda.repository.ProductRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceImplTest {

    @InjectMocks
    private  ProductServiceImpl productService;

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private ProductRepository productRepository;

    @Test
    void shouldReturnAllProducts() {

        List<Product> products = List.of(
                new Product(1L, "Laptop", BigDecimal.valueOf(1200)),
                new Product(2L, "Mouse", BigDecimal.valueOf(1000))
        );
        Mockito.when(productRepository.findAll()).thenReturn(products);
        //act
        List<ProductDto>result = productService.findAllProducts();

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("Laptop", result.get(0).getName());
        assertEquals("Mouse", result.get(1).getName());

        verify(productRepository).findAll();
    }

    @Test
    void shouldReturnEmptyListWhenNoProductsExist(){
        when(productRepository.findAll()).thenReturn(Collections.emptyList());
        List<ProductDto> result = productService.findAllProducts();
        assertThat(result).isEmpty();
    }

    @Test
    void shouldReturnFindProductById(){
        Product product = new Product(1L,"PC",new BigDecimal(100));

        when(productRepository.findById(1L)).thenReturn(Optional.of(product));

        ProductDto result = productService.findProductById(1L);
        verify(productRepository).findById(1L);
        assertEquals("PC",result.getName());
    }

    @Test
    void shouldReturnProductNotFound(){
        when(productRepository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(ProductNotFoundException.class,() -> productService.findProductById(99L));
    }

    @Test
    void shouldReturnProductCreate(){

        ProductCreateDto productDto =  new ProductCreateDto("PC","Ordenador para oficina"
                ,new BigDecimal(200L),10,1L,"Tecnologia");

        Category category = new Category(1L, "Tecnologia", "Dispositivos tecnologicos");

        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));

        Product product=  new Product();
        product.setId(1L);
        product.setName("PC");
        product.setDescription("Ordenador para oficinaS");
        product.setPrice(new BigDecimal(1000L));
        product.setCategory(category);

        when(productRepository.save(any(Product.class))).thenReturn(product);

        ProductDto result = productService.createProduct(productDto);

        assertThat(result.getId().equals(1L));
        assertThat(result.getName().equals("PC"));
    }


    @Test
    void shouldReturnProductUpdate_success() {

        Long id = 1L;
        ProductUpdateDto productUpdate = new ProductUpdateDto("newPC"
                , "Computador gamer", new BigDecimal(100L), 10, 1L);

        Category category = new Category(1L, "Tecnologia", "Dispositivos tecnologicos");

        // Producto existente en la BD
        Product existingProduct = new Product();
        existingProduct.setId(id);
        existingProduct.setName("Old PC");
        existingProduct.setDescripcion("Computador viejo");
        existingProduct.setPrice(new BigDecimal(50L));
        existingProduct.setStock(5);
        existingProduct.setCategory(category);

        Product updatedProduct = new Product();
        updatedProduct.setId(id);
        updatedProduct.setName(productUpdate.getName());
        updatedProduct.setDescripcion(productUpdate.getDescription());
        updatedProduct.setPrice(productUpdate.getPrice());
        updatedProduct.setStock(productUpdate.getStock());
        updatedProduct.setCategory(category);

        when(productRepository.findById(id)).thenReturn(Optional.of(existingProduct));
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));
        when(productRepository.save(any(Product.class))).thenReturn(updatedProduct);

        ProductDto result = productService.updateProduct(id, productUpdate);

        assertThat(result.getName()).isEqualTo("newPC");
        assertThat(result.getDescription()).isEqualTo("Computador gamer");
        assertThat(result.getPrice()).isEqualByComparingTo(new BigDecimal(100));
        assertThat(result.getStock()).isEqualTo(10);

        verify(productRepository).save(any(Product.class));
    }

    //----------------------------STOCK TEST-----------------------------------


    @Test
    void adjustStock_success() {
        Long id = 1L;
        int  quantityChange = 1;

        Product product = new Product(1L,"PC","Computadora Gamer",new BigDecimal(100),12);
        when(productRepository.findById(id)).thenReturn(Optional.of(product));
        productService.adjustStock(id,quantityChange);
        assertEquals(13,product.getStock());

        verify(productRepository, times(1)).save(product);
    }

    @Test
    void increaseStock_success(){
        Long id = 1L;
        int quantityToAdd = 5;


        Product product = new Product(1L, "PC", "Computadora Gamer"
                , new BigDecimal(100), 10);
        when(productRepository.findById(id)).thenReturn(Optional.of(product));
        productService.increaseStock(id, quantityToAdd);

        assertEquals(15, product.getStock());
        verify(productRepository, times(1)).save(product);
    }


    @ParameterizedTest
    @ValueSource(ints = { -10, -1, 0 }) // Prueba los 3 valores
    void increaseStock_throwsExceptionForInvalidQuantities(int invalidQuantity) {

        Long id = 1L;


        assertThrows(InvalidStockQuantityException.class, () -> {
            productService.increaseStock(id, invalidQuantity);
        });
    }

    @Test
    void isStockAvailable_success(){
            Long id = 1L;
            int quantity = 3;

            Product product = new Product(1L, "PC", "Gamer", new BigDecimal(100), 6);
            when(productRepository.findById(id)).thenReturn(Optional.of(product));
            boolean stockDisponible = productService.isStockAvailable(id, quantity);
            assertThat(stockDisponible).isTrue();
    }
}