package com.drunkenlion.alcoholfriday.domain.item.dao;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ItemProductRepositoryImpl implements ItemProductRepositoryCustom {
    private final JPAQueryFactory jpaQueryFactory;
}
