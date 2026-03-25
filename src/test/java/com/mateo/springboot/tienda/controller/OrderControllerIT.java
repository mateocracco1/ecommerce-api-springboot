package com.mateo.springboot.tienda.controller;

import static org.assertj.core.api.Assertions.assertThat;

import com.mateo.springboot.tienda.BaseIntegrationTest;
import com.mateo.springboot.tienda.models.Category;
import com.mateo.springboot.tienda.models.Product;
import com.mateo.springboot.tienda.models.Role;
import com.mateo.springboot.tienda.models.User;
import com.mateo.springboot.tienda.repository.CategoryRepository;
import com.mateo.springboot.tienda.repository.ProductRepository;
import com.mateo.springboot.tienda.repository.UserRepository;
import com.mateo.springboot.tienda.security.CustomUserDetails;
import com.mateo.springboot.tienda.service.cart.CartService;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;

import java.math.BigDecimal;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
public class OrderControllerIT extends BaseIntegrationTest {


    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private CartService cartService;

    @Autowired
    private EntityManager entityManager;



    @Test
    @Transactional
    void deberiaRealizarCheckoutDesdeElControlador() throws Exception {

        User usuario = new User("pepito", "pepito@email.com", "123456", Role.CUSTOMER,true);
        usuario = userRepository.save(usuario);

        Category cat = categoryRepository.save(new Category("Electronica", "Detalle"));

        Product p = productRepository.save(new Product("Mouse", new BigDecimal("100"), 10, cat));


        cartService.addProduct(usuario, p.getId(), 2);

        entityManager.flush();
        entityManager.clear();

        CustomUserDetails customUser = new CustomUserDetails(usuario);

        mockMvc.perform(post("/api/orders/checkout")
                        .with(user(customUser)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.total").value(200.0))
                .andExpect(jsonPath("$.status").value("CREATED"));

        var productoEnBD = productRepository.findById(p.getId()).get();
        assertThat(productoEnBD.getStock()).isEqualTo(8);
    }


}
