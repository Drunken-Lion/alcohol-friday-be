package com.drunkenlion.alcoholfriday.domain.item.entity;

import com.drunkenlion.alcoholfriday.domain.product.entity.Product;
import com.drunkenlion.alcoholfriday.global.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.Comment;

@Entity
@Getter
@SuperBuilder(toBuilder = true)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "item_product")
public class ItemProduct extends BaseEntity {
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id", columnDefinition = "BIGINT", foreignKey = @ForeignKey(value = ConstraintMode.NO_CONSTRAINT))
    private Item item;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", columnDefinition = "BIGINT", foreignKey = @ForeignKey(value = ConstraintMode.NO_CONSTRAINT))
    private Product product;

    @Comment("제품의 재고를 없애는 수량")
    @Column(name = "quantity", columnDefinition = "BIGINT")
    private Long quantity;

    // 연관 관계 편의 메서드
    public void addItem(Item item) {
        this.item = item;
        this.item.getItemProducts().add(this);
    }

    // 연관 관계 편의 메서드
    public void addProduct(Product product) {
        this.product = product;
        this.product.getItemProducts().add(this);
    }
}
