package com.mateo.springboot.tienda.repository;

import com.mateo.springboot.tienda.models.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product,Long> {

    List<Product> findByStockLessThanEqual(int threshold);
    boolean existsByName(String name);

}
