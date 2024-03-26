package com.drunkenlion.alcoholfriday.domain.admin.restaurant.order.entity;

import com.drunkenlion.alcoholfriday.domain.admin.restaurant.order.enumerated.RestaurantOrderStatus;
import com.drunkenlion.alcoholfriday.domain.admin.restaurant.order.util.RestaurantOrderStatusConverter;
import com.drunkenlion.alcoholfriday.domain.member.entity.Member;
import com.drunkenlion.alcoholfriday.domain.restaurant.restaurant.entity.Restaurant;
import com.drunkenlion.alcoholfriday.global.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.Comment;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@SuperBuilder
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "restaurant_order")
public class RestaurantOrder extends BaseEntity {
    @Column(name = "status", columnDefinition = "VARCHAR(20)")
    @Comment("주문 상태정보")
    @Convert(converter = RestaurantOrderStatusConverter.class)
    private RestaurantOrderStatus orderStatus;

    @Builder.Default
    @Column(name = "total_price", columnDefinition = "DECIMAL(64, 3)")
    @Comment("주문 총 금액")
    private BigDecimal totalPrice = BigDecimal.ZERO;

    @Column(name = "address", columnDefinition = "VARCHAR(200)")
    @Comment("배송지 주소")
    private String address;

    @Comment("배송지 상세 주소")
    @Column(name = "address_detail", columnDefinition = "VARCHAR(200)")
    private String addressDetail;

    @Comment("배송시 주의사항")
    @Column(name = "description", columnDefinition = "MEDIUMTEXT")
    private String description;

    @Comment("배송지 우편번호")
    @Column(name = "postcode", columnDefinition = "VARCHAR(50)")
    private String postcode;

    @Column(name = "recipient", columnDefinition = "VARCHAR(50)")
    @Comment("배송받는 사람")
    private String recipient;

    @Column(name = "phone", columnDefinition = "BIGINT")
    @Comment("배송받는 사람의 연락처")
    private Long phone;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "restaurant_id", columnDefinition = "BIGINT", foreignKey = @ForeignKey(value = ConstraintMode.NO_CONSTRAINT))
    private Restaurant restaurant;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", columnDefinition = "BIGINT", foreignKey = @ForeignKey(value = ConstraintMode.NO_CONSTRAINT))
    private Member member;

    @Builder.Default
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "restaurantOrder")
    public List<RestaurantOrderDetail> details = new ArrayList<>();

    public void addTotalPrice(BigDecimal totalPrice) {
        this.totalPrice = this.totalPrice.add(totalPrice);
    }

    public void updateOrders(String description, String recipient, Long phone) {
        this.description = description;
        this.phone = phone;
        this.recipient = recipient;
        this.orderStatus = RestaurantOrderStatus.WAITING_APPROVAL;
    }

    public void updateStatus(RestaurantOrderStatus status) {
        this.orderStatus = status;
    }

    public String getFullAddress() {
        return this.address + " " + this.addressDetail + " [" + this.postcode + "]";
    }
}
