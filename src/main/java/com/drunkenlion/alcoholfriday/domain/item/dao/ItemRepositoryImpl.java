package com.drunkenlion.alcoholfriday.domain.item.dao;

import com.drunkenlion.alcoholfriday.domain.item.entity.Item;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.drunkenlion.alcoholfriday.domain.category.entity.QCategory.category;
import static com.drunkenlion.alcoholfriday.domain.item.entity.QItem.item;
import static com.drunkenlion.alcoholfriday.domain.item.entity.QItemProduct.itemProduct;
import static com.drunkenlion.alcoholfriday.domain.product.entity.QProduct.product;

@RequiredArgsConstructor
public class ItemRepositoryImpl implements ItemRepositoryCustom {
    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public Page<Item> search(List<String> keywordType, String keyword, Pageable pageable) {
        BooleanBuilder builder = new BooleanBuilder();

        if (!keyword.isBlank()) {
            List<BooleanExpression> conditions = new ArrayList<>();

            // TODO: hashtag에 대한 부분 명확하게 이야기 하지 않음... 일단 미 구현
            if (keywordType.contains("type")) conditions.add(category.lastName.contains(keyword));
            if (keywordType.contains("name")) conditions.add(item.name.contains(keyword));

            conditions.stream()
                    .reduce(BooleanExpression::or)
                    .ifPresent(builder::and);
        }

        List<Item> items = jpaQueryFactory
                .select(item)
                .from(item)
                .leftJoin(category).on(category.id.eq(item.category.id)).fetchJoin()
                .where(builder)
                .limit(pageable.getPageSize())
                .fetch();

        JPAQuery<Long> total = jpaQueryFactory
                .select(item.count())
                .from(item)
                .leftJoin(category).on(category.id.eq(item.category.id)).fetchJoin()
                .where(builder);

        return PageableExecutionUtils.getPage(items, pageable, total::fetchOne);
    }

    @Override
    public Optional<Item> get(Long id) {
        return Optional.ofNullable(
                jpaQueryFactory
                        .select(item)
                        .from(item)
                        .join(itemProduct).on(itemProduct.item.id.eq(item.id)).fetchJoin()
                        .join(product).on(product.id.eq(itemProduct.product.id)).fetchJoin()
                        .join(category).on(category.id.eq(product.category.id)).fetchJoin()
                        .where(item.id.eq(id))
                        .fetchOne()
        );
    }
}
