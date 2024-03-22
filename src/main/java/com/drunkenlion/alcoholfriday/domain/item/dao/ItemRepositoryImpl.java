package com.drunkenlion.alcoholfriday.domain.item.dao;

import com.drunkenlion.alcoholfriday.domain.item.entity.Item;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;

import java.util.List;
import java.util.Optional;

import static com.drunkenlion.alcoholfriday.domain.category.entity.QCategory.category;
import static com.drunkenlion.alcoholfriday.domain.category.entity.QCategoryClass.categoryClass;
import static com.drunkenlion.alcoholfriday.domain.item.entity.QItem.item;
import static com.drunkenlion.alcoholfriday.domain.item.entity.QItemProduct.itemProduct;
import static com.drunkenlion.alcoholfriday.domain.maker.entity.QMaker.maker;
import static com.drunkenlion.alcoholfriday.domain.product.entity.QProduct.product;

@RequiredArgsConstructor
public class ItemRepositoryImpl implements ItemRepositoryCustom {
    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public Page<Item> search(List<String> categories, String keyword, Pageable pageable) {
        // 카테고리 검색 조건 생성
        BooleanExpression categoryPredicate = categories.isEmpty()
                ? item.isNotNull() // 카테고리 조건이 없는 경우 기본적으로 모든 아이템을 선택
                : categories.stream()
                .map(category.lastName::eq) // categoryLastName -> category.lastName.eq(categoryLastName)
                .reduce(BooleanExpression::or)
                .orElse(null);

        // 키워드 검색 조건 생성 - keyword가 없는 경우 카테고리로만 검색
        BooleanExpression searchPredicate = keyword.isBlank()
                ? categoryPredicate
                : categoryPredicate.and(item.name.contains(keyword))
                .or(categoryPredicate.and(product.name.contains(keyword)))
                .or(categoryPredicate.and(maker.name.contains(keyword)));

        List<Item> items = jpaQueryFactory
                .select(item)
                .from(item)
                .leftJoin(itemProduct).on(item.eq(itemProduct.item))
                .leftJoin(product).on(itemProduct.product.eq(product))
                .leftJoin(maker).on(product.maker.eq(maker))
                .leftJoin(category).on(item.category.eq(category))
                .where(searchPredicate)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        JPAQuery<Long> total = jpaQueryFactory
                .select(item.count())
                .from(item)
                .leftJoin(itemProduct).on(item.eq(itemProduct.item))
                .leftJoin(product).on(itemProduct.product.eq(product))
                .leftJoin(maker).on(product.maker.eq(maker))
                .leftJoin(category).on(item.category.eq(category))
                .where(searchPredicate);

        return PageableExecutionUtils.getPage(items, pageable, total::fetchOne);
    }

    @Override
    public Optional<Item> get(Long id) {
        return Optional.ofNullable(
                jpaQueryFactory
                        .select(item)
                        .from(item)
                        .leftJoin(category).on(category.id.eq(item.category.id)).fetchJoin()
                        .leftJoin(categoryClass).on(categoryClass.id.eq(category.categoryClass.id)).fetchJoin()
                        .leftJoin(itemProduct).on(itemProduct.item.id.eq(item.id)).fetchJoin()
                        .leftJoin(product).on(product.id.eq(itemProduct.product.id)).fetchJoin()
                        .leftJoin(maker).on(maker.id.eq(product.maker.id)).fetchJoin()
                        .where(item.id.eq(id))
                        .fetchOne()
        );
    }
}
