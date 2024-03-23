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
    @Comment("제품 이름")
    @Column(name = "name", columnDefinition = "VARCHAR(50)")
    private String name;

    @Comment("제품 원가")
    @Column(name = "price", columnDefinition = "DECIMAL(64, 3)")
    private BigDecimal price;

    @Comment("유통 가격")
    @Column(name = "distribution_price", columnDefinition = "DECIMAL(64, 3)")
    private BigDecimal distributionPrice;

    @Comment("재고 수량")
    @Column(name = "quantity", columnDefinition = "BIGINT")
    private Long quantity;

    @Comment("술 도수")
    @Column(name = "alcohol", columnDefinition = "DOUBLE")
    private Double alcohol;

    @Comment("제품 재료")
    @Column(name = "ingredient", columnDefinition = "VARCHAR(1000)")
    private String ingredient;

    @Comment("술 단맛")
    @Column(name = "sweet", columnDefinition = "BIGINT")
    private Long sweet;

    @Comment("술 신맛")
    @Column(name = "sour", columnDefinition = "BIGINT")
    private Long sour;

    @Comment("술 청량감")
    @Column(name = "cool", columnDefinition = "BIGINT")
    private Long cool;

    @Comment("술 바디감")
    @Column(name = "body", columnDefinition = "BIGINT")
    private Long body;

    @Comment("술 밸런스")
    @Column(name = "balance", columnDefinition = "BIGINT")
    private Long balance;

    @Comment("술 향기")
    @Column(name = "incense", columnDefinition = "BIGINT")
    private Long incense;

    @Comment("술 목넘김")
    @Column(name = "throat", columnDefinition = "BIGINT")
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

    public void updateQuantity(Long quantity) {
        this.quantity = quantity;
    }

    public void minusQuantity(Long quantity) {
        this.quantity = this.quantity - quantity;
    }
    public void plusQuantity(Long quantity) {
        this.quantity = this.quantity + quantity;
    }
}
