package com.mateo.springboot.tienda.repository;

import com.mateo.springboot.tienda.models.Cart;
import com.mateo.springboot.tienda.models.StatusCart;
import com.mateo.springboot.tienda.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CartRepository extends JpaRepository<Cart,Long> {


    Optional<Cart> findByUserAndStatus(User user, StatusCart status);

}
