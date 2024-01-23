package com.drunkenlion.alcoholfriday.domain.payment.entity;

import com.drunkenlion.alcoholfriday.domain.member.entity.Member;
import com.drunkenlion.alcoholfriday.domain.order.entity.Order;
import com.drunkenlion.alcoholfriday.global.common.PaymentMethod;
import com.drunkenlion.alcoholfriday.global.common.PaymentStatus;
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
@Table(name = "payment")
public class Payment extends BaseEntity {
    @Column(name = "status", columnDefinition = "VARCHAR(20)")
    @Comment("결제 상태정보")
    @Enumerated(EnumType.STRING)
    private PaymentStatus paymentStatus;

    @Column(name = "method", columnDefinition = "VARCHAR(20)")
    @Comment("결제 수단")
    @Enumerated(EnumType.STRING)
    private PaymentMethod paymentMethod;

    @Column(name = "order_price", columnDefinition = "DECIMAL(64, 3)")
    @Comment("배송 금액")
    private BigDecimal orderPrice;

    @Column(name = "delivery_price", columnDefinition = "DECIMAL(64, 3)")
    @Comment("배송 금액")
    private BigDecimal deliveryPrice;

    @Column(name = "total_price", columnDefinition = "DECIMAL(64, 3)")
    @Comment("결제 총 금액")
    private BigDecimal totalPrice;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", columnDefinition = "BIGINT", foreignKey = @ForeignKey(value = ConstraintMode.NO_CONSTRAINT))
    private Order order;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", columnDefinition = "BIGINT", foreignKey = @ForeignKey(value = ConstraintMode.NO_CONSTRAINT))
    private Member member;
}
