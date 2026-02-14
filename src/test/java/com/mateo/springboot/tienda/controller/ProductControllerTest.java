package com.mateo.springboot.tienda.controller;

import com.mateo.springboot.tienda.dto.product.ProductCreateDto;
import com.mateo.springboot.tienda.dto.product.ProductDto;
import com.mateo.springboot.tienda.dto.product.ProductUpdateDto;
import com.mateo.springboot.tienda.dto.user.UserDto;
import com.mateo.springboot.tienda.models.Product;
import com.mateo.springboot.tienda.service.product.ProductService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;


@WebMvcTest(ProductController.class)
@AutoConfigureMockMvc(addFilters = false)
class ProductControllerTest {


    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ProductService productService;


    @Test
    void getProducts_shouldReturnAllProducts() throws Exception {

        List<ProductDto> products = List.of(
                new ProductDto(1L, "Mouse Gamer", "Mouse RGB", new BigDecimal("2500"), 10, 1L, "Periféricos"),
                new ProductDto(2L, "Teclado", "Teclado mecánico", new BigDecimal("15000"), 5, 1L, "Periféricos"),
                new ProductDto(3L, "Monitor", "144hz 27 pulgadas", new BigDecimal("120000"), 2, 2L, "Monitores")
        );

//            when(productService.findAllProducts()).thenReturn(products);
//

            mockMvc.perform(get("/api/products").contentType(APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.length()").value(3))
                    .andExpect(jsonPath("$[2].name").value("Monitor"))
                    .andExpect(jsonPath("$[2].price").value("120000"));

            Mockito.verify(productService).findAllProducts();

    }


    @Test
    void getProductById_ShouldReutrnProductById() throws Exception {

         ProductDto productDto = new ProductDto(1L,"PS5","Consola",new BigDecimal("1400"),20,1L,"Tecnologia");

//         when(productService.findProductById(1L)).thenReturn(productDto);

        mockMvc.perform(get("/api/products/{id}",1L).contentType(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("PS5"))
                .andExpect(jsonPath("$.categoryName").value("Tecnologia"));

        verify(productService).findProductById(1L);
    }
    @Test
    void createProduct_shouldReturnProductDto() throws Exception {
        // DTO que envía el cliente
        ProductCreateDto createDto = new ProductCreateDto(
                "Xbox X",
                "Consola de videoJuegos",
                new BigDecimal("1400"),
                10,
                2L,
                "Tecnologia"
        );

        // DTO que devuelve el servicio (lo que se envía al cliente)
        ProductDto returnedDto = new ProductDto(
                1L,
                "Xbox X",
                "Consola de videoJuegos",
                new BigDecimal("1400"),
                10,
                2L,
                "Tecnologia"
        );

//        when(productService.createProduct(Mockito.any(ProductCreateDto.class)))
//                .thenReturn(returnedDto);

        mockMvc.perform(
                post("/api/products")
                        .contentType(APPLICATION_JSON)
                        .content("""
                            {
                                "name": "Xbox X",
                                "description": "Consola de videoJuegos",
                                "price": 1400,
                                "stock": 10,
                                "categoryId": 2,
                                "categoryName": "Tecnologia"
                            }
                            """)
        ).andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Xbox X"))
                .andExpect(jsonPath("$.categoryName").value("Tecnologia"));


        Mockito.verify(productService).createProduct(Mockito.any(ProductCreateDto.class));

    }


    @Test
    void updateProduct_ShouldReturnProductUpdate() throws Exception {

        ProductDto returnedDto = new ProductDto(
                1L,
                "PC Gamer",
                "Computadora Gamer",
                new BigDecimal("500"),
                3,
                1L,
                "Tecnologia"
        );
//
//        when(productService.updateProduct(Mockito.eq(1L), Mockito.any(ProductUpdateDto.class)))
//                .thenReturn(returnedDto);

        mockMvc.perform(
                        put("/api/products/1")
                                .contentType(APPLICATION_JSON)
                                .content("""
                            {
                                "name": "PC Gamer",
                                "description": "Computadora Gamer",
                                "price": 500,
                                "stock": 3,
                                "categoryId": 1
                            }
                            """)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("PC Gamer"))
                .andExpect(jsonPath("$.description").value("Computadora Gamer"))
                .andExpect(jsonPath("$.categoryId").value(1L));

        Mockito.verify(productService).updateProduct(Mockito.eq(1L), Mockito.any(ProductUpdateDto.class));

    }

    @Test
    void deleteProduct_ShouldDeleteProduct() throws Exception {

        mockMvc.perform(delete("/api/products/1"))
                .andExpect(status().isNoContent());

        Mockito.verify(productService).deleteProductById(1L);

    }


}