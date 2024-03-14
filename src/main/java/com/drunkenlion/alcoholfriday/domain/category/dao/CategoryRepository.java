package com.drunkenlion.alcoholfriday.domain.category.dao;

import com.drunkenlion.alcoholfriday.domain.category.entity.Category;
import com.drunkenlion.alcoholfriday.domain.category.entity.CategoryClass;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CategoryRepository extends JpaRepository<Category, Long> {
    Optional<Category> findByIdAndDeletedAtIsNull(Long categoryLastId);

    List<Category> findByCategoryClassAndDeletedAtIsNull(CategoryClass categoryClass);

    Optional<Category> findByLastName(String lastName);
}
