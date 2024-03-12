package com.drunkenlion.alcoholfriday.domain.cart.entity;

import com.drunkenlion.alcoholfriday.domain.item.entity.Item;
import com.drunkenlion.alcoholfriday.global.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.Comment;

@Entity
@Getter
@SuperBuilder(toBuilder = true)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "cart_detail")
public class CartDetail extends BaseEntity {
    @Comment("장바구니 정보")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cart_id", columnDefinition = "BIGINT", foreignKey = @ForeignKey(value = ConstraintMode.NO_CONSTRAINT))
    private Cart cart;

    @Comment("장바구니에 담긴 상품")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id", columnDefinition = "BIGINT", foreignKey = @ForeignKey(value = ConstraintMode.NO_CONSTRAINT))
    private Item item;

    @Comment("장바구니에 담긴 상품 수량")
    @ColumnDefault("0")
    @Column(name = "quantity", columnDefinition = "BIGINT")
    private Long quantity;

    public void addItem(Item item) {
        this.item = item;
        cart.getCartDetails().add(this);
    }

    public void addQuantity(Long quantity) {
        this.quantity = quantity;
    }
}
