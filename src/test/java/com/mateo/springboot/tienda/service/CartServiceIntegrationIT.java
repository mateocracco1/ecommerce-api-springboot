package com.mateo.springboot.tienda.service;

import com.mateo.springboot.tienda.BaseIntegrationTest;
import com.mateo.springboot.tienda.models.Category;
import com.mateo.springboot.tienda.models.Product;
import com.mateo.springboot.tienda.models.Role;
import com.mateo.springboot.tienda.models.User;
import com.mateo.springboot.tienda.repository.CategoryRepository;
import com.mateo.springboot.tienda.repository.ProductRepository;
import com.mateo.springboot.tienda.repository.UserRepository;
import com.mateo.springboot.tienda.service.cart.CartService;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

public class CartServiceIntegrationIT extends BaseIntegrationTest {


    @Autowired
    private CartService cartService;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CategoryRepository categoryRepository;


    @Autowired
    private EntityManager entityManager;

    @Test
    @Transactional
    void deberiaAgregarProductoAlCarritoCorrectamente(){


        User usuario = new User();
        usuario.setUsername("pepito");
        usuario.setEmail("pepito@email.com");
        usuario.setPassword("123456");
        usuario.setEnabled(true);
         usuario.setRole(Role.CUSTOMER);
        usuario = userRepository.save(usuario);

        Category category = new Category();
        category.setName("Cables");
        categoryRepository.save(category);

        Product cable = new Product();
        cable.setName("Cable doble");
        cable.setPrice(new BigDecimal("200"));
        cable.setStock(10);
        cable.setCategory(category);
        productRepository.save(cable);

        cartService.addProduct(usuario, cable.getId(), 2);

        //sincronizar mysql y limpiar memoira
        entityManager.flush();
        entityManager.clear();

        var carritoActualizado = cartService.getActiveCart(usuario);

        assertThat(carritoActualizado.getItems()).hasSize(1);

        assertThat(cartService.calculateSubtotal(carritoActualizado))
                .isEqualByComparingTo(new BigDecimal("400"));

    }


}
