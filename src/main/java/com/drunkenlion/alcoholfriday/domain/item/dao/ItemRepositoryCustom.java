package com.drunkenlion.alcoholfriday.domain.item.dao;

import com.drunkenlion.alcoholfriday.domain.item.entity.Item;
import com.querydsl.core.Tuple;
import org.hibernate.annotations.Nationalized;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface ItemRepositoryCustom {
    Page<Item> search(List<String> keywordType, String keyword, Pageable pageable);
    Optional<Item> get(Long id);
}
