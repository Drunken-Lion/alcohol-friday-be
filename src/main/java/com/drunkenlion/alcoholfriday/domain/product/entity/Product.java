package com.drunkenlion.alcoholfriday.domain.product.entity;

import com.drunkenlion.alcoholfriday.domain.category.entity.Category;
import com.drunkenlion.alcoholfriday.domain.item.entity.ItemProduct;
import com.drunkenlion.alcoholfriday.domain.maker.entity.Maker;
import com.drunkenlion.alcoholfriday.global.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.Comment;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@SuperBuilder(toBuilder = true)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "product")
public class Product extends BaseEntity {
    @Column(name = "name", columnDefinition = "VARCHAR(50)")
    @Comment("제품 이름")
    private String name;

    @Column(name = "price", columnDefinition = "DECIMAL(64, 3)")
    @Comment("제품 원가")
    private BigDecimal price;

    @Column(name = "quantity", columnDefinition = "BIGINT")
    @Comment("재고 수량")
    private Long quantity;

    @Column(name = "alcohol", columnDefinition = "BIGINT")
    @Comment("술 도수")
    private Long alcohol;

    @Column(name = "ingredient", columnDefinition = "VARCHAR(1000)")
    @Comment("제품 재료")
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

    @Column(name = "balance", columnDefinition = "BIGINT")
    @Comment("술 밸런스")
    private Long balance;

    @Column(name = "incense", columnDefinition = "BIGINT")
    @Comment("술 향기")
    private Long incense;

    @Column(name = "throat", columnDefinition = "BIGINT")
    @Comment("술 목넘김")
    private Long throat;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", columnDefinition = "BIGINT", foreignKey = @ForeignKey(value = ConstraintMode.NO_CONSTRAINT))
    private Category category;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "maker_id", columnDefinition = "BIGINT", foreignKey = @ForeignKey(value = ConstraintMode.NO_CONSTRAINT))
    private Maker maker;

    @OneToMany(mappedBy = "product")
    @Builder.Default
    private List<ItemProduct> itemProducts = new ArrayList<>();

    // 연관 관계 편의 메서드
    public void addCategory(Category category) {
        this.category = category;
        category.getProducts().add(this);
    }
}
