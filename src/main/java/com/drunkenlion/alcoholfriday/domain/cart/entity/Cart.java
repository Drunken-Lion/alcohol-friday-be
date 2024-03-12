package com.drunkenlion.alcoholfriday.domain.cart.entity;

import com.drunkenlion.alcoholfriday.domain.member.entity.Member;
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
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "cart")
public class Cart extends BaseEntity {
    @Comment("장바구니 주인")
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", columnDefinition = "BIGINT", foreignKey = @ForeignKey(value = ConstraintMode.NO_CONSTRAINT))
    private Member member;

    @Builder.Default
    @Comment("장바구니에 담긴 상품(들)")
    @OneToMany(mappedBy = "cart")
    @ToString.Exclude
    private List<CartDetail> cartDetails = new ArrayList<>();

    /**
     * @deprecated Builder로 객체가 생성되도록 하기 위해 삭제될 예정입니다.
     */
    @Deprecated
    public static Cart create(Member member) {
        Cart cart = new Cart();
        cart.createCart(member);
        return cart;
    }

    @Deprecated
    private void createCart(Member member) {
        this.member = member;
    }

    public BigDecimal getTotalCartPrice(List<CartDetail> cartDetails) {
        if (cartDetails.isEmpty()) {
            return BigDecimal.ZERO;
        } else {
            return cartDetails.stream()
                    .map(cartDetail -> cartDetail.getItem().getPrice())
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
        }
    }

    public Long getTotalCartQuantity(List<CartDetail> cartDetails) {
        if (cartDetails.isEmpty()) {
            return 0L;
        } else {
            return cartDetails.stream()
                    .mapToLong(CartDetail::getQuantity)
                    .sum();
        }
    }
}
