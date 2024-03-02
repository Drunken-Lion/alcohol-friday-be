package com.drunkenlion.alcoholfriday.domain.order.entity;

import com.drunkenlion.alcoholfriday.domain.member.entity.Member;
import com.drunkenlion.alcoholfriday.global.common.entity.BaseEntity;
import com.drunkenlion.alcoholfriday.global.common.enumerated.OrderStatus;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.Comment;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Entity
@Getter
@SuperBuilder
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "orders")
public class Order extends BaseEntity {
    @Column(name = "order_no", columnDefinition = "VARCHAR(200)")
    @Comment("주문 고유번호")
    private String orderNo;

    @Column(name = "status", columnDefinition = "VARCHAR(20)")
    @Comment("주문 상태정보")
    @Enumerated(EnumType.STRING)
    private OrderStatus orderStatus;

    @Column(name = "price", columnDefinition = "DECIMAL(64, 3)")
    @Comment("주문 총 금액")
    private BigDecimal price;

    @Column(name = "recipient", columnDefinition = "VARCHAR(50)")
    @Comment("배송받는 사람")
    private String recipient;

    @Column(name = "phone", columnDefinition = "BIGINT")
    @Comment("배송받는 사람의 연락처")
    private Long phone;

    @Column(name = "address", columnDefinition = "VARCHAR(200)")
    @Comment("배송지 주소")
    private String address;

    @Column(name = "detail", columnDefinition = "VARCHAR(200)")
    @Comment("배송지 상세 주소")
    private String detail;

    @Column(name = "description", columnDefinition = "MEDIUMTEXT")
    @Comment("배송시 주의사항")
    private String description;

    @Column(name = "postcode", columnDefinition = "BIGINT")
    @Comment("배송지 우편번호")
    private Long postcode;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", columnDefinition = "BIGINT", foreignKey = @ForeignKey(value = ConstraintMode.NO_CONSTRAINT))
    private Member member;

    @OneToMany(mappedBy = "order")
    @Builder.Default
    private List<OrderDetail> orderDetails = new ArrayList<>();

    public void addMember(Member member) {
        this.member = member;
        member.getOrders().add(this);
    }

    public void genOrderNo(Long id) {
        // TODO orderNo를 주문 접수할 때 만들고 클라이언트에 내려주기 (결제 요청 전)
        // yyyy-MM-dd 형식의 DateTimeFormatter 생성
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        this.orderNo = getCreatedAt().format(formatter) + "__" + id;
    }

    public void addPrice(List<OrderDetail> orderDetailList) {
        this.price = getTotalOrderPrice(orderDetailList);
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
}
