package com.mateo.springboot.tienda.service;

import com.mateo.springboot.tienda.BaseIntegrationTest;
import com.mateo.springboot.tienda.dto.order.OrderDto;
import com.mateo.springboot.tienda.models.*;
import com.mateo.springboot.tienda.repository.CategoryRepository;
import com.mateo.springboot.tienda.repository.ProductRepository;
import com.mateo.springboot.tienda.repository.UserRepository;
import com.mateo.springboot.tienda.service.cart.CartService;
import com.mateo.springboot.tienda.service.order.OrderService;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.util.List;

public class OrderServiceIT extends BaseIntegrationTest {

    @Autowired private OrderService orderService;

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

    User crearUsuarioDePrueba(){
        User usuario = new User();
        usuario.setUsername("pepito");
        usuario.setEmail("pepito@email.com");
        usuario.setPassword("123456");
        usuario.setEnabled(true);
        usuario.setRole(Role.CUSTOMER);
        usuario = userRepository.save(usuario);
        return  usuario;
    }

    @Test
    @Transactional
    void deberiaRealizarCheckoutExitosamente() {
        // 1. Arrange: Preparamos el escenario real
        User usuario = crearUsuarioDePrueba(); // Método auxiliar para no ensuciar el test

        Category cat = new Category();
        cat.setName("Electronica");
        cat.setDescription("todo con cablees y esas cosas");
        categoryRepository.save(cat);

        entityManager.flush();

        Product p1 = new Product("Mouse", new BigDecimal("100"), 10, cat);
        Product p2 = new Product("Teclado", new BigDecimal("500"), 5, cat);
        productRepository.saveAll(List.of(p1, p2));

        entityManager.flush();
        entityManager.clear();

        cartService.addProduct(usuario, p1.getId(), 2); // 200 total
        cartService.addProduct(usuario, p2.getId(), 1); // 500 total

        entityManager.flush();
        entityManager.clear();
        OrderDto ordenRealizada = orderService.checkout(usuario.getId());

        assertThat(ordenRealizada).isNotNull();
        assertThat(ordenRealizada.getTotal()).isEqualByComparingTo(new BigDecimal("700"));
        assertThat(ordenRealizada.getStatus()).isEqualTo(OrderStatus.CREATED);

        assertThat(productRepository.findById(p1.getId()).get().getStock()).isEqualTo(8);
        assertThat(productRepository.findById(p2.getId()).get().getStock()).isEqualTo(4);

        Cart cartDespues = cartService.getActiveCart(usuario);

        assertThat(cartDespues.getItems()).isEmpty();
    }



}
