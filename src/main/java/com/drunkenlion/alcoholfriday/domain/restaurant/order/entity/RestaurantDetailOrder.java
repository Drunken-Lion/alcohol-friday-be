package com.drunkenlion.alcoholfriday.domain.restaurant.order.entity;

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
public class RestaurantDetailOrder extends BaseEntity {
    @Column(name = "quantity", columnDefinition = "BIGINT")
    @Comment("주문 수량")
    private Long quantity;

    @Column(name = "price", columnDefinition = "DECIMAL(64, 3)")
    @Comment("주문 시 제품 1개당 가격")
    private BigDecimal price;

    @Column(name = "total_price", columnDefinition = "DECIMAL(64, 3)")
    @Comment("총 주문 금액")
    private BigDecimal totalPrice;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "restaurant_order_id", columnDefinition = "BIGINT", foreignKey = @ForeignKey(value = ConstraintMode.NO_CONSTRAINT))
    private RestaurantOrder restaurantOrder;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", columnDefinition = "BIGINT", foreignKey = @ForeignKey(value = ConstraintMode.NO_CONSTRAINT))
    private Product product;
}
