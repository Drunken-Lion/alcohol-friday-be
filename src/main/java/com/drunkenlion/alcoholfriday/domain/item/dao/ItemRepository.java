package com.drunkenlion.alcoholfriday.domain.item.dao;

import com.drunkenlion.alcoholfriday.domain.category.entity.Category;
import com.drunkenlion.alcoholfriday.domain.item.entity.Item;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ItemRepository extends JpaRepository<Item, Long>, ItemRepositoryCustom {
    Optional<Item> findByIdAndDeletedAtIsNull(Long id);
    boolean existsByCategoryAndDeletedAtIsNull(Category category);
    boolean existsByCategoryInAndDeletedAtIsNull(List<Category> categories);

    Optional<Item> findByInfo(String info);
}
