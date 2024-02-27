package com.drunkenlion.alcoholfriday.domain.item.dao;

import com.drunkenlion.alcoholfriday.domain.item.entity.Item;
import com.drunkenlion.alcoholfriday.domain.item.entity.ItemProduct;
import com.drunkenlion.alcoholfriday.domain.product.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ItemProductRepository extends JpaRepository<ItemProduct, Long>, ItemProductRepositoryCustom {
    boolean existsByProductAndDeletedAtIsNull(Product product);
    List<ItemProduct> findByItemAndDeletedAtIsNull(Item item);
}
