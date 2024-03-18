package com.drunkenlion.alcoholfriday.domain.restaurant.refund.entity;

import com.drunkenlion.alcoholfriday.domain.restaurant.order.entity.RestaurantOrder;
import com.drunkenlion.alcoholfriday.domain.restaurant.refund.enumerated.RestaurantOrderRefundStatus;
import com.drunkenlion.alcoholfriday.domain.restaurant.refund.util.RestaurantOrderRefundStatusConverter;
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
@Table(name = "restaurant_order_refund")
public class RestaurantOrderRefund extends BaseEntity {
    @Comment("레스토랑 주문 정보")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "restaurantOrder_id", columnDefinition = "BIGINT", foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private RestaurantOrder restaurantOrder;

    @Comment("환불 총 가격")
    @Column(name = "total_price", columnDefinition = "DECIMAL(64, 3)")
    private BigDecimal totalPrice;

    @Comment("사장 환불 사유")
    @Column(name = "owner_reason", columnDefinition = "VARCHAR(200)")
    private String ownerReason;

    @Comment("관리자 반려 사유")
    @Column(name = "admin_reason", columnDefinition = "VARCHAR(200)")
    private String adminReason;

    @Comment("환불 상태 정보")
    @Column(name = "status", columnDefinition = "VARCHAR(20)")
    @Convert(converter = RestaurantOrderRefundStatusConverter.class)
    private RestaurantOrderRefundStatus status;
}
