package com.drunkenlion.alcoholfriday.domain.admin.restaurant.refund.entity;

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

@Getter
@SuperBuilder(toBuilder = true)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "restaurant_order_refund_detail")
public class RestaurantOrderRefundDetail extends BaseEntity {
    @Comment("반품 정보")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "restaurant_order_refund_id", columnDefinition = "BIGINT", foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private RestaurantOrderRefund restaurantOrderRefund;

    @Comment("제품 정보")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", columnDefinition = "BIGINT", foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private Product product;

    @Comment("제품 당 주문 환불 수량")
    @Column(name = "quantity", columnDefinition = "BIGINT")
    private Long quantity;

    @Comment("제품 1개당 환불 가격")
    @Column(name = "price", columnDefinition = "DECIMAL(64, 3)")
    private BigDecimal price;

    @Comment("제품 당 총 환불 가격")
    @Column(name = "total_price", columnDefinition = "DECIMAL(64, 3)")
    private BigDecimal totalPrice;

    public void addOrderRefund(RestaurantOrderRefund orderRefund) {
        this.restaurantOrderRefund = orderRefund;
        orderRefund.getRestaurantOrderRefundDetails().add(this);
    }
}
