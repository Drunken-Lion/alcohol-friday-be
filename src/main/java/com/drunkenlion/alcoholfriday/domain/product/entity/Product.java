package com.drunkenlion.alcoholfriday.domain.product.entity;

import com.drunkenlion.alcoholfriday.domain.category.entity.Category;
import com.drunkenlion.alcoholfriday.domain.maker.entity.Maker;
import com.drunkenlion.alcoholfriday.global.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.Comment;

@Entity
@Getter
@SuperBuilder
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "product")
public class Product extends BaseEntity {
    @Column(name = "name", columnDefinition = "VARCHAR(50)")
    @Comment("제품 이름")
    private String name;

    @Column(name = "quantity", columnDefinition = "BIGINT")
    @Comment("재고 수량")
    private Long quantity;

    @Column(name = "alcohol", columnDefinition = "BIGINT")
    @Comment("술 도수")
    private Long alcohol;

    @Column(name = "ingredient", columnDefinition = "VARCHAR(1000)")
    @Comment("제품 이름")
    private String ingredient;

    @Column(name = "sweet", columnDefinition = "BIGINT")
    @Comment("술 단맛")
    private Long sweet;

    @Column(name = "sour", columnDefinition = "BIGINT")
    @Comment("술 신맛")
    private Long sour;

    @Column(name = "cool", columnDefinition = "BIGINT")
    @Comment("술 청량감")
    private Long cool;

    @Column(name = "body", columnDefinition = "BIGINT")
    @Comment("술 바디감")
    private Long body;

    @Column(name = "balence", columnDefinition = "BIGINT")
    @Comment("술 밸런스")
    private Long balence;

    @Column(name = "insense", columnDefinition = "BIGINT")
    @Comment("술 향기")
    private Long insense;

    @Column(name = "throat", columnDefinition = "BIGINT")
    @Comment("술 목넘김")
    private Long throat;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", columnDefinition = "BIGINT", foreignKey = @ForeignKey(value = ConstraintMode.NO_CONSTRAINT))
    private Category category;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "maker_id", columnDefinition = "BIGINT", foreignKey = @ForeignKey(value = ConstraintMode.NO_CONSTRAINT))
    private Maker maker;
}
