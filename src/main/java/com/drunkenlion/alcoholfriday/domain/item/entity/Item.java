package com.drunkenlion.alcoholfriday.domain.item.entity;

import com.drunkenlion.alcoholfriday.global.common.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.Comment;

import java.math.BigDecimal;

@Entity
@Getter
@SuperBuilder
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "item")
public class Item extends BaseEntity {
    @Column(name = "name", columnDefinition = "VARCHAR(200)")
    @Comment("상품 이름")
    private String name;

    @Column(name = "price", columnDefinition = "DECIMAL(64, 3)")
    @Comment("상품 가격")
    private BigDecimal price;

    @Column(name = "info", columnDefinition = "MEDIUMTEXT")
    @Comment("상품 설명")
    private BigDecimal info;
}
