package com.daniil.bookstore.bookservice.repository.category;

import com.daniil.bookstore.bookservice.model.Category;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Category, Long> {
    List<Category> findAll();
}
