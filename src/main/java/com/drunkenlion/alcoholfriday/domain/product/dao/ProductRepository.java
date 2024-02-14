package com.drunkenlion.alcoholfriday.domain.product.dao;

import com.drunkenlion.alcoholfriday.domain.maker.entity.Maker;
import com.drunkenlion.alcoholfriday.domain.product.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, Long> {
    boolean existsByMakerAndDeletedAtIsNull(Maker maker);
}
