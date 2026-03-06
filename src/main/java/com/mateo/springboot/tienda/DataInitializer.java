package com.mateo.springboot.tienda;

import com.mateo.springboot.tienda.models.Category;
import com.mateo.springboot.tienda.models.Product;
import com.mateo.springboot.tienda.models.Role;
import com.mateo.springboot.tienda.models.User;
import com.mateo.springboot.tienda.repository.CategoryRepository;
import com.mateo.springboot.tienda.repository.ProductRepository;
import com.mateo.springboot.tienda.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.math.BigDecimal;

@Configuration
public class DataInitializer {

    @Bean
    CommandLineRunner init(UserRepository userRepository,
                           CategoryRepository categoryRepository,
                           ProductRepository productRepository) {
        return args -> {

            // 1. Crear Admin (El que ya tenías)
            if(!userRepository.existsByUsername("admin1")) {
                User admin = new User();
                admin.setUsername("admin1");
                admin.setEmail("admin@example.com");
                admin.setPassword(new BCryptPasswordEncoder().encode("admin123"));
                admin.setRole(Role.ADMIN);
                userRepository.save(admin);
                System.out.println("Admin user created");
            }

            // 2. Crear Cliente (Para poder probar el checkout)
            if(!userRepository.existsByUsername("cliente1")) {
                User cliente = new User();
                cliente.setUsername("cliente1");
                cliente.setEmail("cliente@example.com");
                cliente.setPassword(new BCryptPasswordEncoder().encode("cliente123"));
                cliente.setRole(Role.CUSTOMER);
                userRepository.save(cliente);
                System.out.println("Customer user created");
            }

            // 3. Crear Categorías y Productos solo si la base está vacía
            if(categoryRepository.count() == 0) {

                // --- Categoría 1: Hardware ---
                Category catHardware = new Category();
                catHardware.setName("Hardware");
                catHardware.setDescription("Componentes internos de PC");
                categoryRepository.save(catHardware);

                Product p1 = new Product();
                p1.setName("Memoria RAM 16GB");
                p1.setDescription("Ideal para multitarea");
                p1.setPrice(new BigDecimal("45000.00"));
                p1.setStock(20);
                p1.setCategory(catHardware);
                productRepository.save(p1);

                Product p2 = new Product();
                p2.setName("Disco SSD 1TB");
                p2.setDescription("Almacenamiento ultra rápido");
                p2.setPrice(new BigDecimal("85000.00"));
                p2.setStock(15);
                p2.setCategory(catHardware);
                productRepository.save(p2);

                // --- Categoría 2: Periféricos ---
                Category catPerifericos = new Category();
                catPerifericos.setName("Periféricos");
                catPerifericos.setDescription("Accesorios externos");
                categoryRepository.save(catPerifericos);

                Product p3 = new Product();
                p3.setName("Teclado Mecánico");
                p3.setDescription("Switches red, ideal para programar");
                p3.setPrice(new BigDecimal("60000.00"));
                p3.setStock(30);
                p3.setCategory(catPerifericos);
                productRepository.save(p3);

                Product p4 = new Product();
                p4.setName("Mouse Inalámbrico");
                p4.setDescription("Diseño ergonómico");
                p4.setPrice(new BigDecimal("25000.00"));
                p4.setStock(50);
                p4.setCategory(catPerifericos);
                productRepository.save(p4);

                System.out.println("Categorías y Productos de prueba creados!");
            }
        };
    }
}