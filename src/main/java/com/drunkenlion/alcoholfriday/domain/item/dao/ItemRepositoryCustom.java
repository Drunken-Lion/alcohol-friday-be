package com.drunkenlion.alcoholfriday.domain.item.dao;

import com.drunkenlion.alcoholfriday.domain.item.entity.Item;
import com.querydsl.core.Tuple;
import org.hibernate.annotations.Nationalized;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ItemRepositoryCustom {
    public Page<Item> search(List<String> keywordType, String keyword, Pageable pageable);
}
