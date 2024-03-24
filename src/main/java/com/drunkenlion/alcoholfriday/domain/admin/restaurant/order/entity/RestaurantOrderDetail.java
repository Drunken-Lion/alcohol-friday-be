package com.drunkenlion.alcoholfriday.domain.admin.restaurant.order.entity;

import com.drunkenlion.alcoholfriday.domain.product.entity.Product;
import com.drunkenlion.alcoholfriday.global.common.entity.BaseEntity;
import jakarta.persistence.*;
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
@Table(name = "restaurant_order_detail")
public class RestaurantOrderDetail extends BaseEntity {
    @Comment("주문 수량")
    @Column(name = "quantity", columnDefinition = "BIGINT")
    private Long quantity;

    @Comment("주문 시 제품 1개당 가격")
    @Column(name = "price", columnDefinition = "DECIMAL(64, 3)")
    private BigDecimal price;

    @Comment("총 주문 금액")
    @Column(name = "total_price", columnDefinition = "DECIMAL(64, 3)")
    private BigDecimal totalPrice;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "restaurant_order_id", columnDefinition = "BIGINT", foreignKey = @ForeignKey(value = ConstraintMode.NO_CONSTRAINT))
    private RestaurantOrder restaurantOrder;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", columnDefinition = "BIGINT", foreignKey = @ForeignKey(value = ConstraintMode.NO_CONSTRAINT))
    private Product product;

    public void addOrder(RestaurantOrder order) {
        this.restaurantOrder = order;
        order.getDetails().add(this);
    }
}
