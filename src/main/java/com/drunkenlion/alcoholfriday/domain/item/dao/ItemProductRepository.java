package com.drunkenlion.alcoholfriday.domain.item.dao;

import com.drunkenlion.alcoholfriday.domain.item.entity.ItemProduct;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ItemProductRepository extends JpaRepository<ItemProduct, Long>, ItemProductRepositoryCustom {
}
