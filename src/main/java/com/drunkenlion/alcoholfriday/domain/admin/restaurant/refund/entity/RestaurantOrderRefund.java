package com.drunkenlion.alcoholfriday.domain.admin.restaurant.refund.entity;

import com.drunkenlion.alcoholfriday.domain.admin.restaurant.order.entity.RestaurantOrder;
import com.drunkenlion.alcoholfriday.domain.admin.restaurant.refund.enumerated.RestaurantOrderRefundStatus;
import com.drunkenlion.alcoholfriday.domain.admin.restaurant.refund.util.RestaurantOrderRefundStatusConverter;
import com.drunkenlion.alcoholfriday.domain.restaurant.restaurant.entity.Restaurant;
import com.drunkenlion.alcoholfriday.global.common.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.ConstraintMode;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.Comment;

@Getter
@SuperBuilder(toBuilder = true)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "restaurant_order_refund")
public class RestaurantOrderRefund extends BaseEntity {
    @Comment("레스토랑 정보")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "restaurant_id", columnDefinition = "BIGINT", foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private Restaurant restaurant;

    @Comment("레스토랑 주문 정보")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "restaurant_order_id", columnDefinition = "BIGINT", foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
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

    @OneToMany(mappedBy = "restaurantOrderRefund")
    @Builder.Default
    private List<RestaurantOrderRefundDetail> restaurantOrderRefundDetails = new ArrayList<>();

    public void updateStatus(RestaurantOrderRefundStatus status) {
        this.status = status;
    }

    public void updateAdminReason(String adminReason) {
        this.adminReason = adminReason;
    }
}
