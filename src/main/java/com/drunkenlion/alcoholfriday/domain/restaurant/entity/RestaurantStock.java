package com.drunkenlion.alcoholfriday.domain.restaurant.entity;

import com.drunkenlion.alcoholfriday.domain.product.entity.Product;
import com.drunkenlion.alcoholfriday.global.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.Comment;

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

    public void addRestaurant(Restaurant restaurant) {
        this.restaurant = restaurant;
        restaurant.getRestaurantStocks().add(this);
    }

    public void plusQuantity(Long quantity) {
        this.quantity = this.quantity + quantity;
    }
}
