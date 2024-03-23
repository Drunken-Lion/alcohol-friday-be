package com.drunkenlion.alcoholfriday.domain.restaurant.restaurant.entity;

import com.drunkenlion.alcoholfriday.domain.product.entity.Product;
import com.drunkenlion.alcoholfriday.global.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.Comment;

import java.math.BigDecimal;

@Getter
@Entity
@SuperBuilder(toBuilder = true)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "restaurantStock")
public class RestaurantStock extends BaseEntity {
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", columnDefinition = "BIGINT", foreignKey = @ForeignKey(value = ConstraintMode.NO_CONSTRAINT))
    private Product product;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "restaurant_id", columnDefinition = "BIGINT", foreignKey = @ForeignKey(value = ConstraintMode.NO_CONSTRAINT))
    private Restaurant restaurant;

    @Builder.Default
    @Comment("레스토랑 재고 수량")
    @Column(name = "quantity", columnDefinition = "BIGINT")
    private Long quantity = 0L;

    @Builder.Default
    @Comment("제품 판매 단가")
    @Column(name = "price", columnDefinition = "BIGINT")
    private BigDecimal price = BigDecimal.ZERO;

    public void addRestaurant(Restaurant restaurant) {
        this.restaurant = restaurant;
        restaurant.getRestaurantStocks().add(this);
    }

    public void plusQuantity(Long quantity) {
        this.quantity += quantity;
    }

    public void minusQuantity(Long quantity) {
        this.quantity -= quantity;
    }

}
