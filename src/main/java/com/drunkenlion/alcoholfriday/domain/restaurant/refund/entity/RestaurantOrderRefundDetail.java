package com.drunkenlion.alcoholfriday.domain.restaurant.refund.entity;

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
public class RestaurantOrderRefundDetail extends BaseEntity {
    @Comment("반품 정보")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "restaurantOrderRefund_id", foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private RestaurantOrderRefund restaurantOrderRefund;

    @Comment("상품 정보")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private Product product;

    @Comment("제품 수량")
    private Long quantity;

    @Comment("환불 제품 가격")
    private BigDecimal price;

    @Comment("환불 총 가격")
    private BigDecimal totalPrice;
}
