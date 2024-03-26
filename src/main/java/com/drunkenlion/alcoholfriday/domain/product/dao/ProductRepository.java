package com.drunkenlion.alcoholfriday.domain.product.dao;

import com.drunkenlion.alcoholfriday.domain.category.entity.Category;
import com.drunkenlion.alcoholfriday.domain.maker.entity.Maker;
import com.drunkenlion.alcoholfriday.domain.product.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, Long> {
    boolean existsByMakerAndDeletedAtIsNull(Maker maker);

    Optional<Product> findByIdAndDeletedAtIsNull(Long productId);

    boolean existsByCategoryAndDeletedAtIsNull(Category category);

    boolean existsByCategoryInAndDeletedAtIsNull(List<Category> categories);

    Optional<Product> findByName(String name);

    Page<Product> findAllByDeletedAtIsNull(Pageable pageable);
}
