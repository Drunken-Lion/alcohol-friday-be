package com.drunkenlion.alcoholfriday.domain.category.dao;

import com.drunkenlion.alcoholfriday.domain.category.entity.CategoryClass;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CategoryClassRepository extends JpaRepository<CategoryClass, Long> {
    Optional<CategoryClass> findByIdAndDeletedAtIsNull(Long id);
}
