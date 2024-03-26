package com.drunkenlion.alcoholfriday.domain.admin.restaurant.cart.entity;

import com.drunkenlion.alcoholfriday.domain.product.entity.Product;
import com.drunkenlion.alcoholfriday.global.common.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.ConstraintMode;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.Comment;

@Entity
@Getter
@SuperBuilder(toBuilder = true)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "restaurant_order_cart_detail")
public class RestaurantOrderCartDetail extends BaseEntity {
    @Comment("매장 장바구니 제품 당 수량")
    @Builder.Default
    @Column(name = "quantity", columnDefinition = "BIGINT")
    private Long quantity = 0L;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "restaurant_order_cart_id", columnDefinition = "BIGINT", foreignKey = @ForeignKey(value = ConstraintMode.NO_CONSTRAINT))
    private RestaurantOrderCart restaurantOrderCart;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", columnDefinition = "BIGINT", foreignKey = @ForeignKey(value = ConstraintMode.NO_CONSTRAINT))
    private Product product;

    public void minusQuantity(Long quantity) {
        this.quantity = this.quantity - quantity;
    }

    public void plusQuantity(Long quantity) {
        this.quantity = this.quantity + quantity;
    }

    public void updateQuantity(Long quantity) {
        this.quantity = quantity;
    }

    public void deleteQuantity() {
        this.quantity = 0L;
    }

    public void addCart(RestaurantOrderCart cart) {
        this.restaurantOrderCart = cart;
        this.restaurantOrderCart.getRestaurantDetailOrders().add(this);
    }
}
