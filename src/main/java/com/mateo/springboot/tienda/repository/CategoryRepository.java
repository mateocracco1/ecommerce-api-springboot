package com.mateo.springboot.tienda.repository;

import com.mateo.springboot.tienda.models.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CategoryRepository  extends JpaRepository<Category,Long> {


}
