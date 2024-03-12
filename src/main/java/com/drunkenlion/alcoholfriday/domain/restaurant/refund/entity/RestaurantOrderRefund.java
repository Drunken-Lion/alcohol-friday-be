package com.drunkenlion.alcoholfriday.domain.restaurant.refund.entity;

import com.drunkenlion.alcoholfriday.domain.restaurant.order.entity.RestaurantOrder;
import com.drunkenlion.alcoholfriday.domain.restaurant.order.util.RestaurantOrderStatusConverter;
import com.drunkenlion.alcoholfriday.domain.restaurant.refund.enumerated.RestaurantOrderRefundStatus;
import com.drunkenlion.alcoholfriday.domain.restaurant.refund.util.RestaurantOrderRefundStatusConverter;
import com.drunkenlion.alcoholfriday.global.common.entity.BaseEntity;
import jakarta.persistence.ConstraintMode;
import jakarta.persistence.Convert;
import jakarta.persistence.Converter;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import java.math.BigDecimal;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.Comment;

@Getter
@SuperBuilder(toBuilder = true)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class RestaurantOrderRefund extends BaseEntity {
    @Comment("레스토랑 주문 정보")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "restaurantOrder_id", foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private RestaurantOrder restaurantOrder;

    @Comment("환불 총 가격")
    private BigDecimal totalPrice;

    @Comment("사장 환불 사유")
    private String ownerReason;

    @Comment("관리자 반려 사유")
    private String adminReason;

    @Comment("환불 상태 정보")
    @Convert(converter = RestaurantOrderRefundStatusConverter.class)
    private RestaurantOrderRefundStatus status;
}
