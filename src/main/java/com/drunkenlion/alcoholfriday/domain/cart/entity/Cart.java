package com.drunkenlion.alcoholfriday.domain.cart.entity;

import com.drunkenlion.alcoholfriday.domain.member.entity.Member;
import com.drunkenlion.alcoholfriday.global.common.entity.BaseEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.Comment;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@SuperBuilder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class Cart extends BaseEntity {
    @Comment("장바구니 주인")
    @OneToOne(fetch = FetchType.LAZY)
    private Member member;

    @Builder.Default
    @Comment("장바구니에 담긴 상품(들)")
    @OneToMany(mappedBy = "cart")
    private List<CartDetail> cartDetails = new ArrayList<>();

    /**
     * @deprecated Builder로 객체가 생성되도록 하기 위해 삭제될 예정입니다.
     */
    @Deprecated
    // Member가 첫 장바구니 관련 기능을 사용할 때 추가해 주시면 됩니다.
    public static Cart create(Member member) {
        Cart cart = new Cart();
        cart.createCart(member);
        return cart;
    }

    @Deprecated
    private void createCart(Member member) {
        this.member = member;
    }
}
