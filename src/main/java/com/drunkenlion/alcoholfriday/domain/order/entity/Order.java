package com.drunkenlion.alcoholfriday.domain.order.entity;

import com.drunkenlion.alcoholfriday.domain.member.entity.Member;
import com.drunkenlion.alcoholfriday.domain.order.dto.request.OrderAddressRequest;
import com.drunkenlion.alcoholfriday.domain.order.util.OrderUtil;
import com.drunkenlion.alcoholfriday.global.common.entity.BaseEntity;
import com.drunkenlion.alcoholfriday.global.common.enumerated.OrderStatus;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.Comment;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@SuperBuilder(toBuilder = true)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "orders")
public class Order extends BaseEntity {
    @Comment("주문 고유번호")
    @Column(name = "order_no", columnDefinition = "VARCHAR(200)")
    private String orderNo;

    @Comment("주문 상태정보")
    @Column(name = "status", columnDefinition = "VARCHAR(20)")
    @Enumerated(EnumType.STRING)
    private OrderStatus orderStatus;

    @Comment("주문 상품 총 금액")
    @Column(name = "price", columnDefinition = "DECIMAL(64, 3)")
    private BigDecimal price;

    @Comment("배송 금액")
    @Column(name = "delivery_price", columnDefinition = "DECIMAL(64, 3)")
    private BigDecimal deliveryPrice;

    @Comment("배송비 포함 주문 총 금액")
    @Column(name = "total_price", columnDefinition = "DECIMAL(64, 3)")
    private BigDecimal totalPrice;

    @Comment("배송받는 사람")
    @Column(name = "recipient", columnDefinition = "VARCHAR(50)")
    private String recipient;

    @Comment("배송받는 사람의 연락처")
    @Column(name = "phone", columnDefinition = "BIGINT")
    private Long phone;

    @Comment("배송지 주소")
    @Column(name = "address", columnDefinition = "VARCHAR(200)")
    private String address;

    @Comment("배송지 상세 주소")
    @Column(name = "address_detail", columnDefinition = "VARCHAR(200)")
    private String addressDetail ;

    @Comment("배송시 주의사항")
    @Column(name = "description", columnDefinition = "MEDIUMTEXT")
    private String description;

    @Comment("배송지 우편번호")
    @Column(name = "postcode", columnDefinition = "VARCHAR(50)")
    private String postcode;

    @Comment("주문 취소 사유")
    @Column(name = "cancel_reason", columnDefinition = "VARCHAR(200)")
    private String cancelReason;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", columnDefinition = "BIGINT", foreignKey = @ForeignKey(value = ConstraintMode.NO_CONSTRAINT))
    private Member member;

    @OneToMany(mappedBy = "order")
    @Builder.Default
    private List<OrderDetail> orderDetails = new ArrayList<>();

    public void genOrderNo() {
        // orderNo를 주문 접수할 때 만들고 클라이언트에 내려주기 (결제 요청 전)
        StringBuilder orderNo = new StringBuilder();

        String date = OrderUtil.date.getDate(getCreatedAt());
        String time = OrderUtil.date.getTime();
        String timeMillis = OrderUtil.date.getTimeMillis(getCreatedAt());

        orderNo.append(date).append("-");
        orderNo.append(time).append("-");
        orderNo.append(timeMillis).append("-");
        orderNo.append(this.getId());

        this.orderNo = orderNo.toString();
    }

    public void addPrice(List<OrderDetail> orderDetailList) {
        this.price = getTotalOrderPrice(orderDetailList);
    }

    public void addTotalPrice() {
        this.totalPrice = this.getPrice().add(this.getDeliveryPrice());
    }

    public BigDecimal getTotalOrderPrice(List<OrderDetail> orderDetailList) {
        if (orderDetailList.isEmpty()) {
            return BigDecimal.ZERO;
        } else {
            return orderDetailList.stream()
                    .map(OrderDetail::getTotalPrice)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
        }
    }

    public Long getTotalOrderQuantity(List<OrderDetail> orderDetailList) {
        if (orderDetailList.isEmpty()) {
            return 0L;
        } else {
            return orderDetailList.stream()
                    .mapToLong(OrderDetail::getQuantity)
                    .sum();
        }
    }

    public void updateOrderAddress(OrderAddressRequest orderAddressRequest) {
        this.recipient = orderAddressRequest.getRecipient();
        this.phone = orderAddressRequest.getPhone();
        this.address = orderAddressRequest.getAddress();
        this.addressDetail = orderAddressRequest.getAddressDetail();
        this.description = orderAddressRequest.getDescription();
        this.postcode = orderAddressRequest.getPostcode();
    }

    public void updateOrderStatus(OrderStatus orderStatus) {
        this.orderStatus = orderStatus;
    }

    public void updateCancel(String cancelReason) {
        this.orderStatus = OrderStatus.CANCELLED;
        this.cancelReason = cancelReason;
    }
}
