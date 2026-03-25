package com.mateo.springboot.tienda.repository;

import com.mateo.springboot.tienda.BaseIntegrationTest;
import com.mateo.springboot.tienda.models.Category;
import com.mateo.springboot.tienda.models.Product;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

public class ProductRepositoryIT extends BaseIntegrationTest {

    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private CategoryRepository categoryRepository;

    @Test
    void deberiaEncontrarProductoPorNombre() {

        Category category = new Category();
        category.setName("Cables");
        categoryRepository.save(category);

        Product cable = new Product();
        cable.setName("Cable doble");
        cable.setPrice(new BigDecimal("200"));
        cable.setStock(10);
        cable.setCategory(category);
        productRepository.save(cable);

        assertThat(productRepository.existsByName("Cable doble")).isTrue();
    }


}
