package com.mateo.springboot.tienda;

import com.mateo.springboot.tienda.models.Role;
import com.mateo.springboot.tienda.models.User;
import com.mateo.springboot.tienda.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Configuration
public class DataInitializer {

    @Bean
    CommandLineRunner init(UserRepository userRepository) {
        return args -> {
            if(!userRepository.existsByUsername("admin1")) {
                User admin = new User();
                admin.setUsername("admin1");
                admin.setEmail("admin@example.com");
                admin.setPassword(new BCryptPasswordEncoder().encode("admin123"));
                admin.setRole(Role.ADMIN);
                userRepository.save(admin);
                System.out.println("Admin user created");
            }
        };
    }
}