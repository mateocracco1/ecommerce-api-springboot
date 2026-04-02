package com.mateo.springboot.tienda.config; // 👈 Te sugiero crear esta carpeta 'config' y meterlo acá

import com.mateo.springboot.tienda.models.Category;
import com.mateo.springboot.tienda.models.Product;
import com.mateo.springboot.tienda.models.Role;
import com.mateo.springboot.tienda.models.User;
import com.mateo.springboot.tienda.repository.CategoryRepository;
import com.mateo.springboot.tienda.repository.ProductRepository;
import com.mateo.springboot.tienda.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.math.BigDecimal;

@Configuration
@Profile("dev")
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(UserRepository userRepository,
                           CategoryRepository categoryRepository,
                           ProductRepository productRepository,
                           PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.categoryRepository = categoryRepository;
        this.productRepository = productRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) throws Exception {
        initUsers();
        initCatalog();
    }

    private void initUsers() {

        if (!userRepository.existsByUsername("admin1")) {
            User admin = new User();
            admin.setUsername("admin1");
            admin.setEmail("admin@example.com");
            admin.setPassword(passwordEncoder.encode("admin123"));
            admin.setRole(Role.ADMIN);
            userRepository.save(admin);
            System.out.println("✅ Admin user created");
        }

        if (!userRepository.existsByUsername("cliente1")) {
            User cliente = new User();
            cliente.setUsername("cliente1");
            cliente.setEmail("cliente@example.com");
            cliente.setPassword(passwordEncoder.encode("cliente123"));
            cliente.setRole(Role.CUSTOMER);
            userRepository.save(cliente);
            System.out.println("✅ Customer user created");
        }
    }

    private void initCatalog() {
        if (categoryRepository.count() == 0) {

//Category 1
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

//Category 2
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
    }
}