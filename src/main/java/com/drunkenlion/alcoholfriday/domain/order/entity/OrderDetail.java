package com.drunkenlion.alcoholfriday.domain.order.entity;

import com.drunkenlion.alcoholfriday.domain.item.entity.Item;
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
@Table(name = "order_detail")
public class OrderDetail extends BaseEntity {
    // TODO 궁금 item의 가격이랑 같은건가?
    @Column(name = "item_price", columnDefinition = "DECIMAL(64, 3)")
    @Comment("상품 단가")
    private BigDecimal itemPrice;

    @Column(name = "quantity", columnDefinition = "BIGINT")
    @Comment("주문 수량")
    private Long quantity;

    @Column(name = "total_price", columnDefinition = "DECIMAL(64, 3)")
    @Comment("상품의 총 금액")
    private BigDecimal totalPrice;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id", columnDefinition = "BIGINT", foreignKey = @ForeignKey(value = ConstraintMode.NO_CONSTRAINT))
    private Item item;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", columnDefinition = "BIGINT", foreignKey = @ForeignKey(value = ConstraintMode.NO_CONSTRAINT))
    private Order order;

    public void addItemTotalPrice(BigDecimal totalPrice) {
        this.totalPrice = totalPrice;
    }
}
