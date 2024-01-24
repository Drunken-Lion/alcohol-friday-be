package com.drunkenlion.alcoholfriday.domain.category.dao;

import com.drunkenlion.alcoholfriday.domain.category.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Category, Long> {
}
